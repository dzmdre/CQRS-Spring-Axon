package org.dzmdre.OrdersService.core.data;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrdersRepository extends MongoRepository<OrderEntity, String> {
    OrderEntity findByOrderId(String orderId);
}
