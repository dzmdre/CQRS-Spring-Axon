package org.dzmdre.UsersService.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.dzmdre.UsersService.core.events.UserCreatedEvent;
import org.dzmdre.core.model.PaymentDetails;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Aggregate
public class UserAggregate {
    private String firstName;
    private String lastName;
    private List<PaymentDetails> listPaymentDetails;

    @AggregateIdentifier
    private String userId;

    @CommandHandler
    public UserAggregate(CreateUserCommand createUserCommand) {
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent();
        BeanUtils.copyProperties(createUserCommand, userCreatedEvent);
        AggregateLifecycle.apply(userCreatedEvent);
    }


    @EventSourcingHandler
    public void on(UserCreatedEvent productCreatedEvent) {
          this.userId = productCreatedEvent.getUserId();
          this.firstName = productCreatedEvent.getFirstName();
          this.lastName = productCreatedEvent.getLastName();
          this.listPaymentDetails = productCreatedEvent.getListPaymentDetails();
    }
}
