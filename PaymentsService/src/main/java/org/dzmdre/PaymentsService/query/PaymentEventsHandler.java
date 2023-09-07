package org.dzmdre.PaymentsService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.dzmdre.PaymentsService.core.data.PaymentEntity;
import org.dzmdre.PaymentsService.core.data.PaymentsRepository;
import org.dzmdre.core.events.PaymentProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("payment-group")
public class PaymentEventsHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentEventsHandler.class);
    private final PaymentsRepository paymentsRepository;

    public PaymentEventsHandler(final PaymentsRepository paymentsRepository){
        this.paymentsRepository = paymentsRepository;
    }

    @EventHandler
    public void  on(PaymentProcessedEvent paymentProcessedEvent){
        LOGGER.info("PaymentProcessedEvent is called for orderId: " + paymentProcessedEvent.getOrderId());
        PaymentEntity paymentEntity = new PaymentEntity();
        BeanUtils.copyProperties(paymentProcessedEvent, paymentEntity);
        paymentsRepository.save(paymentEntity);
    }
}
