package com.example.userAccountService.kafka.service;


import com.example.userAccountService.entity.Account;
import com.example.userAccountService.enums.transaction.TransactionDirection;
import com.example.userAccountService.exceptions.NotFoundException;
import com.example.userAccountService.kafka.dto.BalanceUpdateEvent;
import com.example.userAccountService.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountBalanceConsumer {

    private final AccountRepository accountRepository;
    private final AccountEventPublisher accountEventPublisher;


    @KafkaListener(topics = "balance-update-events",groupId = "account-group")
    @Transactional
    public void consumerBalanceUpdate(BalanceUpdateEvent event) {
            log.info("processing balance update for account {}" , event.getAccountNumber());
            Account account = accountRepository.findByAccountNumber(event.getAccountNumber())
                    .orElseThrow(()-> new NotFoundException("account not found!"));
            if (event.getTransactionDirection()== TransactionDirection.CREDIT) {
                account.setBalance(account.getBalance().add(event.getAmount()));
            } else if (event.getTransactionDirection()== TransactionDirection.DEBIT) {
                account.setBalance(account.getBalance().subtract(event.getAmount()));
            }
            accountRepository.save(account);

            BalanceUpdateEvent balanceUpdateEventToPublish = BalanceUpdateEvent.builder()
                    .email(account.getUser().getEmail())
                    .firstName(account.getUser().getFirstName())
                    .accountNumber(account.getAccountNumber())
                    .amount(event.getAmount())
                    .transactionDirection(event.getTransactionDirection())
                    .reference(event.getReference())
                    .build();
        accountEventPublisher.publishTransactionNotificationEvent(balanceUpdateEventToPublish);
    }

}
