package org.dzmdre.OrdersService.saga;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.axonframework.deadline.DeadlineManager;
import org.dzmdre.OrdersService.events.OrderApprovedEvent;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.dzmdre.OrdersService.command.ApprovedOrderCommand;
import org.dzmdre.OrdersService.command.RejectOrderCommand;
import org.dzmdre.OrdersService.core.model.OrderSummary;
import org.dzmdre.OrdersService.events.OrderCreatedEvent;
import org.dzmdre.OrdersService.events.OrderRejectedEvent;
import org.dzmdre.OrdersService.query.FindOrderQuery;
import org.dzmdre.core.commands.CancelProductReservationCommand;
import org.dzmdre.core.commands.ProcessPaymentCommand;
import org.dzmdre.core.commands.ReserveProductCommand;
import org.dzmdre.core.events.PaymentProcessedEvent;
import org.dzmdre.core.events.ProductReservationCancelledEvent;
import org.dzmdre.core.events.ProductReservedEvent;
import org.dzmdre.core.model.User;
import org.dzmdre.core.query.FetchUserPaymentDetailsQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Saga
public class OrderSaga {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    private final String PAYMENT_PROCESSING_TIMEOUT_DEADLINE="payment-processing-deadline";

    private String scheduleId;

    @StartSaga
    @SagaEventHandler(associationProperty="orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        final String orderId = orderCreatedEvent.getOrderId();
        final String productId = orderCreatedEvent.getProductId();
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderId)
                .productId(productId)
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();
        LOGGER.info("OrderCreatedEvent handled for orderId: " + reserveProductCommand.getOrderId() +
                " and productId: " + productId);
        commandGateway.send(reserveProductCommand, (commandMessage, commandResultMessage) -> {
            if(commandResultMessage.isExceptional()) {
                LOGGER.debug("Start a compensating transaction");
                final RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(orderId,
                        commandResultMessage.exceptionResult().getMessage());
                commandGateway.send(rejectOrderCommand);
            }
        });
    }

    @SagaEventHandler(associationProperty="orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        final String orderId = productReservedEvent.getOrderId();
        final String productId = productReservedEvent.getProductId();
        LOGGER.info("ProductReservedEvent is called for productId: "+ productId +
                " and orderId: " + orderId);
        final FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery =
                new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());
        try {
            final User userPaymentDetails = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
            if(userPaymentDetails == null) {
                cancelProductReservation(productReservedEvent,"Could not fetch user payment details");
                return;
            }
            scheduleId =  deadlineManager.schedule(Duration.of(120, ChronoUnit.SECONDS),
                    PAYMENT_PROCESSING_TIMEOUT_DEADLINE, productReservedEvent);
            LOGGER.info("Successfully fetched user payment details for userId " + userPaymentDetails.getUserId());
            if (CollectionUtils.isEmpty(userPaymentDetails.getPaymentDetailsList())) {
                LOGGER.info("The PaymentDetails is null. Initiating a compensating transaction");
                cancelProductReservation(productReservedEvent, "Could not process, no payment details");
            }
            final ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                    .orderId(productReservedEvent.getOrderId())
                    .paymentDetails(userPaymentDetails.getPaymentDetailsList().get(0))
                    .paymentId(UUID.randomUUID().toString())
                    .build();
            final String result = commandGateway.sendAndWait(processPaymentCommand);
            if(result == null) {
                LOGGER.info("The ProcessPaymentCommand is null. Initiating a compensating transaction");
                cancelProductReservation(productReservedEvent, "Could not process user payment with provided payment details");
            }
        } catch(Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            cancelProductReservation(productReservedEvent,ex.getMessage());
        }
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
        LOGGER.debug("Start compensating transaction");
        cancelDeadline();
        CancelProductReservationCommand publishProductReservationCommand =
                CancelProductReservationCommand.builder()
                        .orderId(productReservedEvent.getOrderId())
                        .userId(productReservedEvent.getUserId())
                        .productId(productReservedEvent.getProductId())
                        .reason(reason)
                        .quantity(productReservedEvent.getQuantity()).build();
        commandGateway.send(publishProductReservationCommand);
    }

    private void cancelDeadline() {
        if (scheduleId != null) {
            deadlineManager.cancelSchedule(PAYMENT_PROCESSING_TIMEOUT_DEADLINE, scheduleId);
            scheduleId = null;
        }
    }

    @SagaEventHandler(associationProperty="orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        cancelDeadline();
        LOGGER.debug("Send an ApproveOrderCommand");
        final ApprovedOrderCommand approvedOrderCommand =
                new ApprovedOrderCommand(paymentProcessedEvent.getOrderId());
        commandGateway.send(approvedOrderCommand);
    }

    @SagaEventHandler(associationProperty="orderId")
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
        LOGGER.debug("Create and send a RejectOrderCommand");
        final RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(productReservationCancelledEvent.getOrderId(),
                 productReservationCancelledEvent.getReason());
        commandGateway.send(rejectOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty="orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        LOGGER.info("Order is approved. Order Saga is complete for orderId: " + orderApprovedEvent.getOrderId());
        queryUpdateEmitter.emit(FindOrderQuery.class, query -> true,
                new OrderSummary(orderApprovedEvent.getOrderId(),
                        orderApprovedEvent.getOrderStatus(),
                        "Order is approved"));
    }

    @EndSaga
    @SagaEventHandler(associationProperty="orderId")
    public void handle(OrderRejectedEvent orderRejectedEvent) {
        LOGGER.info("Successfully rejected order with id " + orderRejectedEvent.getOrderId());
        queryUpdateEmitter.emit(FindOrderQuery.class, query -> true,
                new OrderSummary(orderRejectedEvent.getOrderId(),
                        orderRejectedEvent.getOrderStatus(),
                        orderRejectedEvent.getReason()));
    }

    @DeadlineHandler(deadlineName=PAYMENT_PROCESSING_TIMEOUT_DEADLINE)
    public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
        LOGGER.info("Payment processing deadline took place. Sending a compensating command to cancel the product reservation");
        cancelProductReservation(productReservedEvent, "Payment timeout");
    }
}