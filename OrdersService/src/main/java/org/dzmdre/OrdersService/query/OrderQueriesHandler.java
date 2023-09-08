package org.dzmdre.OrdersService.query;

import org.apache.logging.log4j.util.Strings;
import org.axonframework.queryhandling.QueryHandler;
import org.dzmdre.OrdersService.core.data.OrderEntity;
import org.dzmdre.OrdersService.core.data.OrdersRepository;
import org.dzmdre.OrdersService.core.model.OrderSummary;
import org.springframework.stereotype.Component;

@Component
public class OrderQueriesHandler {

    private OrdersRepository ordersRepository;

    public OrderQueriesHandler(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
        OrderEntity orderEntity = ordersRepository.findByOrderId(findOrderQuery.getOrderId());
        return new OrderSummary(orderEntity.getOrderId(),
                orderEntity.getOrderStatus(), Strings.EMPTY);
    }

}