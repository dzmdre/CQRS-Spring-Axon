package org.dzmdre.core.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class User {
    private final String firstName;
    private final String lastName;
    private final String userId;
    private final List<PaymentDetails> paymentDetailsList;
}
