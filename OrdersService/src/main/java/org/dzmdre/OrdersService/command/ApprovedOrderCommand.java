package org.dzmdre.OrdersService.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.dzmdre.OrdersService.core.OrderStatus;

@Value
public class ApprovedOrderCommand {
    @TargetAggregateIdentifier
    private final String orderId;
    private final OrderStatus orderStatus = OrderStatus.APPROVED;
}
