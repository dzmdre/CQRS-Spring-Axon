package org.dzmdre.UsersService.query;


import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.dzmdre.UsersService.core.data.PaymentDetailsEntity;
import org.dzmdre.UsersService.core.data.UserEntity;
import org.dzmdre.UsersService.core.data.UsersRepository;
import org.dzmdre.UsersService.core.events.UserCreatedEvent;
import org.dzmdre.core.model.PaymentDetails;
import org.dzmdre.core.query.FetchUserPaymentDetailsQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.dzmdre.core.model.User;
import java.util.List;


@Component
@ProcessingGroup("user-group")
public class UserEventsHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventsHandler.class);

    @Autowired
    private UsersRepository usersRepository;

    @EventHandler
    public User findUserPaymentDetails(FetchUserPaymentDetailsQuery query) {
        final UserEntity userEntity = usersRepository.findByUserId(query.getUserId());
        //TODO: UserEntity to user add payment details
        final List<PaymentDetailsEntity> paymentDetailsEntities = userEntity.getListPaymentDetailsEntity();
        final List<PaymentDetails> paymentDetailsList =
                paymentDetailsEntities.stream().map(this::paymentEntityToPaymentDetails).toList();
        return User.builder()
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .userId(query.getUserId())
                .paymentDetailsList(paymentDetailsList)
                .build();
    }

    private PaymentDetails paymentEntityToPaymentDetails(PaymentDetailsEntity entity) {
        return PaymentDetails.builder()
                .cardNumber(entity.getCardNumber())
                .cvv(entity.getCvv())
                .name(entity.getName())
                .validUntilMonth(entity.getValidUntilMonth())
                .validUntilYear(entity.getValidUntilYear())
                .build();
    }

    private PaymentDetailsEntity paymentDetailsToPaymentEntity(PaymentDetails paymentDetails) {
        final PaymentDetailsEntity entity = new PaymentDetailsEntity();
        BeanUtils.copyProperties(paymentDetails, entity);
        return entity;
    }

    @EventHandler
    public void on(UserCreatedEvent event) {
        final UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(event, userEntity, "listPaymentDetails");
        final List<PaymentDetailsEntity> paymentDetailsEntityList = event.getListPaymentDetails().stream()
                .map(this::paymentDetailsToPaymentEntity).toList();
        userEntity.setListPaymentDetailsEntity(paymentDetailsEntityList);
        try {
            usersRepository.save(userEntity);
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
