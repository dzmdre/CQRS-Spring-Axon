package org.dzmdre.UsersService.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Data
@Table(name = "paymentdetails")
public class PaymentDetailsEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 2342343245L;
    @Id
    @Column(unique = true)
    private String cardNumber;
    private String name;
    private int validUntilMonth;
    private int validUntilYear;
    private String cvv;
}
