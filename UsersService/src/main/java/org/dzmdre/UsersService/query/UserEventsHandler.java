package org.dzmdre.UsersService.query;


import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.dzmdre.core.model.PaymentDetails;
import org.dzmdre.core.query.FetchUserPaymentDetailsQuery;
import org.springframework.stereotype.Component;
import org.dzmdre.core.model.User;


@Component
@ProcessingGroup("user-group")
public class UserEventsHandler {
    @EventHandler
    public User findUserPaymentDetails(FetchUserPaymentDetailsQuery query) {
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("SERGEY KARGOPOLOV")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();
        return User.builder()
                .firstName("Sergey")
                .lastName("Kargopolov")
                .userId(query.getUserId())
                .paymentDetails(paymentDetails)
                .build();
    }
}
