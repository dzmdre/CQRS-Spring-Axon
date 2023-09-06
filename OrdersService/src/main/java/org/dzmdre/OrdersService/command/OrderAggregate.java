package org.dzmdre.OrdersService.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.dzmdre.OrdersService.command.rest.CreateOrderCommand;
import org.dzmdre.OrdersService.core.OrderStatus;
import org.dzmdre.OrdersService.events.OrderCreatedEvent;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Aggregate(snapshotTriggerDefinition="orderSnapshotTriggerDefinition")
public class OrderAggregate {
    private int quantity;
    private String userId;
    private String addressId;
    private OrderStatus orderStatus;
    @AggregateIdentifier
    private String orderId;
    private String productId;

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {
        if(createOrderCommand.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be zero");
        }
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
        AggregateLifecycle.apply(orderCreatedEvent);
    }

    //The OrderAggregate class should also have an @EventSourcingHandler method that sets values for all fields in the OrderAggregate.
    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        this.productId = orderCreatedEvent.getProductId();
        this.orderId = orderCreatedEvent.getOrderId();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
        this.addressId = orderCreatedEvent.getAddressId();
        this.userId = orderCreatedEvent.getUserId();
        this.quantity = orderCreatedEvent.getQuantity();
    }
}
