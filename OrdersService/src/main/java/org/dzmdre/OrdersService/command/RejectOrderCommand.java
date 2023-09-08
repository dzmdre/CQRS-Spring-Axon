package org.dzmdre.OrdersService.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class RejectOrderCommand {
    @TargetAggregateIdentifier
    private String orderId;
    private String reason;
}
