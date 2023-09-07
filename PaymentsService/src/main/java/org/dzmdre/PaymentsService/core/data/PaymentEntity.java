package org.dzmdre.PaymentsService.core.data;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;


@Data
@Entity
@Table(name = "payments")
public class PaymentEntity implements Serializable {

    private static final long serialVersionUID = 479713859894403L;

    @Id
    private String paymentId;
    @Column
    public String orderId;
}
