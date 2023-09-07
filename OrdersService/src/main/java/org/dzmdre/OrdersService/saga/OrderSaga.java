package org.dzmdre.OrdersService.saga;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.dzmdre.OrdersService.command.RejectOrderCommand;
import org.dzmdre.OrdersService.events.OrderCreatedEvent;
import org.dzmdre.core.commands.ProcessPaymentCommand;
import org.dzmdre.core.commands.ReserveProductCommand;
import org.dzmdre.core.events.ProductReservedEvent;
import org.dzmdre.core.model.User;
import org.dzmdre.core.query.FetchUserPaymentDetailsQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;

@Saga
public class OrderSaga {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

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
            LOGGER.info("Successfully fetched user payment details for userId " + userPaymentDetails.getUserId());
            final ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                    .orderId(productReservedEvent.getOrderId())
                    .paymentDetails(userPaymentDetails.getPaymentDetails())
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
            return;
        }
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
        //TODO: add cancel order event
        LOGGER.debug("Start compensating transaction");
    }

}