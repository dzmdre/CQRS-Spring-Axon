package org.dzmdre.UsersService.core.data;

import lombok.Data;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    @OneToMany(fetch=FetchType.EAGER)  //TODO: check perfomance!!! for now user is not required without payment details
    @Fetch(value=FetchMode.SELECT)
    private List<PaymentDetailsEntity> listPaymentDetails;
}
