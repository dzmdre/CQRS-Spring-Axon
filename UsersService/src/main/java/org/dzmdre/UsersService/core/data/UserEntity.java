package org.dzmdre.UsersService.core.data;

import lombok.Data;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class UserEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -27987352345L;

    @Id
    @Column(unique = true)
    private String userId;
    private String firstName;
    private String lastName;
    @OneToMany
    private List<PaymentDetailsEntity> listPaymentDetails;
}
