package org.dzmdre.UsersService.core.events;

import lombok.Data;
import org.dzmdre.core.model.PaymentDetails;

import java.util.List;

@Data
public class UserCreatedEvent {
    private String userId;
    private String firstName;
    private String lastName;
    private List<PaymentDetails> listPaymentDetails;
}
