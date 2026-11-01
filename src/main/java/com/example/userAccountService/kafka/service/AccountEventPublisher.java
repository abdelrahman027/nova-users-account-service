package com.example.userAccountService.kafka.service;


import com.example.userAccountService.kafka.dto.BalanceUpdateEvent;
import com.example.userAccountService.kafka.dto.UserRegistrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountEventPublisher {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    private static final String USER_TOPIC = "user-registred-events";
    private static final String BALANCE_UPDATE_TOPIC = "balance-update-notification-events";



    public void publishedUserRegistrationEvent(UserRegistrationEvent event) {

        try {
            log.info("sending message {}" , event.getEmail());

            kafkaTemplate.send(USER_TOPIC,event.getEmail(),event);
        } catch (Exception ex) {
                log.error("error sending kafka message {}" , ex.getMessage());
        }
    }

    public void publishTransactionNotificationEvent(BalanceUpdateEvent event) {
        try {
            log.info("Balance update sending message {}" , event.getEmail());

            kafkaTemplate.send(BALANCE_UPDATE_TOPIC,event.getEmail(),event);
        } catch (Exception ex) {
            log.error(" failed balance update error sending kafka message {}" , ex.getMessage());
        }
    }

}
