package org.dzmdre.OrdersService.command.rest;

import jakarta.validation.Valid;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.dzmdre.OrdersService.command.CreateOrderCommand;
import org.dzmdre.OrdersService.core.OrderStatus;
import org.dzmdre.OrdersService.core.model.OrderSummary;
import org.dzmdre.OrdersService.query.FindOrderQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;


@RestController
@RequestMapping("/orders")
public class OrdersCommandController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrdersCommandController.class);
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    @Autowired
    public OrdersCommandController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }
    @PostMapping
    public OrderSummary createOrder(@Valid @RequestBody CreateOrderRestModel createOrderRestModel) {
        final String orderId = UUID.randomUUID().toString();
        final CreateOrderCommand createOrderCommand = CreateOrderCommand
                .builder()
                .orderId(orderId)
                .orderStatus(OrderStatus.CREATED)
                .quantity(createOrderRestModel.getQuantity())
                .addressId(createOrderRestModel.getAddressId())
                .productId(createOrderRestModel.getProductId())
                .userId(createOrderRestModel.getUserId()).build();

        try (SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult = queryGateway.subscriptionQuery(
                new FindOrderQuery(orderId), ResponseTypes.instanceOf(OrderSummary.class),
                ResponseTypes.instanceOf(OrderSummary.class))) {
            commandGateway.sendAndWait(createOrderCommand);
            final Flux<OrderSummary> orderSummaryFlux = queryResult.updates();
            return orderSummaryFlux.blockFirst();
        } catch (Exception  ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return null;
    }
}
