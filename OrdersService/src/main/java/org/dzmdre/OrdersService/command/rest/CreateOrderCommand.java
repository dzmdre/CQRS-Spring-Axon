package org.dzmdre.OrdersService.command.rest;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.dzmdre.OrdersService.core.OrderStatus;

@Data
@Builder
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    public final String orderId;
    private final String userId;
    private final String productId;
    private final int quantity;
    private final String addressId;
    private final OrderStatus orderStatus;
}
