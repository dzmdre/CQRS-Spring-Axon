package org.dzmdre.OrdersService.events;

import lombok.Data;
import org.dzmdre.OrdersService.core.OrderStatus;

@Data
public class OrderCreatedEvent {
    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;
}
