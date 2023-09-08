package org.dzmdre.UsersService.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.dzmdre.core.model.PaymentDetails;

import java.util.List;

@Builder
@Data
public class CreateUserCommand {
    @TargetAggregateIdentifier
    private String userId;
    private String firstName;
    private String lastName;
    private List<PaymentDetails> listPaymentDetails;
}
