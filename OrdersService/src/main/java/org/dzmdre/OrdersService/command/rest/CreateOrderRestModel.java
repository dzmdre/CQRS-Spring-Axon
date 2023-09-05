package org.dzmdre.OrdersService.command.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRestModel {
    @NotBlank(message="Product is a required field")
    private String productId;
    @NotBlank(message="Address is a required field")
    private String addressId;
    @Min(value=1, message="Quantity cannot be lower than 1")
    @Max(value=5, message="Quantity cannot be larger than 5")
    private Integer quantity;
}
