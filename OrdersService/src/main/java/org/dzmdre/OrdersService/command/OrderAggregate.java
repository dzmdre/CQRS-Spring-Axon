package org.dzmdre.OrdersService.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.dzmdre.OrdersService.core.OrderStatus;
import org.dzmdre.OrdersService.core.data.OrdersRepository;
import org.dzmdre.OrdersService.events.OrderApprovedEvent;
import org.dzmdre.OrdersService.events.OrderCreatedEvent;
import org.dzmdre.OrdersService.events.OrderRejectedEvent;
import org.springframework.beans.BeanUtils;

@Aggregate(snapshotTriggerDefinition="orderSnapshotTriggerDefinition")
public class OrderAggregate {
    private int quantity;
    private String userId;
    private String addressId;
    private OrderStatus orderStatus;
    @AggregateIdentifier
    private String orderId;
    private String productId;
    private final OrdersRepository ordersRepository;

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand,
                          OrdersRepository ordersRepository) {
        if(createOrderCommand.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be zero");
        }
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
        AggregateLifecycle.apply(orderCreatedEvent);
        this.ordersRepository = ordersRepository;
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        this.productId = orderCreatedEvent.getProductId();
        this.orderId = orderCreatedEvent.getOrderId();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
        this.addressId = orderCreatedEvent.getAddressId();
        this.userId = orderCreatedEvent.getUserId();
        this.quantity = orderCreatedEvent.getQuantity();
    }

    @EventSourcingHandler
    public void on(ApprovedOrderCommand approvedOrderCommand) {
        OrderApprovedEvent approvedEvent = new OrderApprovedEvent(approvedOrderCommand.getOrderId());
        AggregateLifecycle.apply(approvedEvent);
    }

    @EventSourcingHandler
    public void on(OrderApprovedEvent orderApprovedEvent) {
        this.orderStatus = orderApprovedEvent.getOrderStatus();
    }

    @EventSourcingHandler
    public void on(RejectOrderCommand rejectOrderCommand) {
        OrderRejectedEvent orderRejectedEvent = new OrderRejectedEvent(rejectOrderCommand.getOrderId(),
                rejectOrderCommand.getReason());
        AggregateLifecycle.apply(orderRejectedEvent);
    }

    @EventSourcingHandler
    public void on(OrderRejectedEvent orderRejectedEvent) {
        this.orderStatus = orderRejectedEvent.getOrderStatus();
    }
}
