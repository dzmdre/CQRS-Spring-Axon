package org.dzmdre.OrdersService.command.rest;

import jakarta.validation.Valid;
import org.dzmdre.OrdersService.command.rest.CreateOrderCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.dzmdre.OrdersService.core.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/orders")
public class OrdersCommandController {
    public static final String USER_ID_CONSTANT = "27b95829-4f3f-4ddf-8983-151ba010e35b";
    private final Environment env;
    private final CommandGateway commandGateway;
    @Autowired
    public OrdersCommandController(Environment env, CommandGateway commandGateway) {
        this.env = env;
        this.commandGateway = commandGateway;
    }
    @PostMapping
    public String createOrder(@Valid @RequestBody CreateOrderRestModel createOrderRestModel) {
        CreateOrderCommand createOrderCommand = CreateOrderCommand
                .builder()
                .orderId(UUID.randomUUID().toString())
                .orderStatus(OrderStatus.CREATED)
                .quantity(createOrderRestModel.getQuantity())
                .addressId(createOrderRestModel.getAddressId())
                .productId(createOrderRestModel.getProductId())
                .userId(USER_ID_CONSTANT).build();
        String returnValue = commandGateway.sendAndWait(createOrderCommand);
        return returnValue;
    }
}
