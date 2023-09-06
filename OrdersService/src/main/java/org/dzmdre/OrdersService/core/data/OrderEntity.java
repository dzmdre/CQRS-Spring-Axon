package org.dzmdre.OrdersService.core.data;

import jakarta.persistence.*;
import lombok.Data;
import org.dzmdre.OrdersService.core.OrderStatus;

import java.io.Serializable;

@Entity
@Data
@Table(name = "orders")
public class OrderEntity implements Serializable {
    private static final long serialVersionUID = -2224352345L;
    @Id
    @Column(unique = true)
    public String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
