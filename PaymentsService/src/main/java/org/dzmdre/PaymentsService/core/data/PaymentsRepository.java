package org.dzmdre.PaymentsService.core.data;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentsRepository extends MongoRepository<PaymentEntity, String> {
}
