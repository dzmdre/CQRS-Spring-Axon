package org.dzmdre.UsersService.command.rest;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.dzmdre.core.model.PaymentDetails;

import java.util.List;

@Data
public class CreateUserRestModel {
    @NotBlank(message = "First Name can't be empty")
    private String firstName;
    @NotBlank(message = "Last Name can't be empty")
    private String lastName;
    @NotEmpty(message = "Payment details required")
    private List<PaymentDetails> listPaymentDetails;
}
