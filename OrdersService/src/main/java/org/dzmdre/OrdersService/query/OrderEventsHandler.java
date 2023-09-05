package org.dzmdre.OrdersService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.dzmdre.OrdersService.core.data.OrderEntity;
import org.dzmdre.OrdersService.events.OrderCreatedEvent;
import org.dzmdre.OrdersService.core.data.OrdersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;


@Component
@ProcessingGroup("order-group")
public class OrderEventsHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventsHandler.class);

    private final OrdersRepository ordersRepository;

    public OrderEventsHandler(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(event, orderEntity);
        try {
            ordersRepository.save(orderEntity);
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Error saving order entity:", ex);
        }
    }
}
