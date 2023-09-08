package org.dzmdre.OrdersService.events;

import jakarta.persistence.criteria.Order;
import lombok.Data;
import lombok.Value;
import org.dzmdre.OrdersService.core.OrderStatus;

@Value
@Data
public class OrderRejectedEvent {
    private final String orderId;
    private final String reason;
    private final OrderStatus orderStatus = OrderStatus.REJECTED;
}
