package org.dzmdre.OrdersService.events;

import lombok.Value;
import org.dzmdre.OrdersService.core.OrderStatus;

@Value
public class OrderApprovedEvent {
    private String orderId;
    private OrderStatus orderStatus =  OrderStatus.APPROVED;
}
