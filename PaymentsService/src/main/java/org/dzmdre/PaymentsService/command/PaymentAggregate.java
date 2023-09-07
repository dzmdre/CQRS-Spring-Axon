package org.dzmdre.PaymentsService.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.dzmdre.core.commands.ProcessPaymentCommand;
import org.dzmdre.core.events.PaymentProcessedEvent;
import org.apache.commons.lang3.Validate;
import org.dzmdre.core.model.PaymentDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Aggregate(snapshotTriggerDefinition="paymentSnapshotTriggerDefinition")
public class PaymentAggregate {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentAggregate.class);
    @AggregateIdentifier
    private String paymentId;
    private String orderId;

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
        final String orderId = processPaymentCommand.getOrderId();
        final String paymentId = processPaymentCommand.getPaymentId();
        final PaymentDetails paymentDetails = processPaymentCommand.getPaymentDetails();
        Validate.notEmpty(orderId, "OrderId can not be empty");
        Validate.notEmpty(paymentId, "PaymentId can not be empty");
        Validate.notNull(paymentDetails, "Payment data can not be empty");
        PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(orderId,
                paymentId);
        try {
            AggregateLifecycle.apply(paymentProcessedEvent);
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Error saving payment entity:", ex);
        }
    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent) {
        this.orderId = paymentProcessedEvent.getOrderId();
        this.paymentId = paymentProcessedEvent.getPaymentId();
    }
}
