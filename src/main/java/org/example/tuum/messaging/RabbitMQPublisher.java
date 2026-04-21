package org.example.tuum.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tuum.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishAccountCreated(AccountCreatedEvent event) {
        log.debug("Publishing account created event: accountId={}", event.accountId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ACCOUNT_CREATED_ROUTING_KEY, event);
    }

    public void publishAccountBalanceCreated(AccountBalanceCreatedEvent event) {
        log.debug("Publishing balance created event: balanceId={}, accountId={}", event.balanceId(), event.accountId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.BALANCE_CREATED_ROUTING_KEY, event);
    }

    public void publishBalanceUpdated(BalanceUpdatedEvent event) {
        log.debug("Publishing balance updated event: balanceId={}, accountId={}", event.balanceId(), event.accountId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.BALANCE_UPDATED_ROUTING_KEY, event);
    }

    public void publishTransactionCreated(TransactionCreatedEvent event) {
        log.debug("Publishing transaction created event: transactionId={}, accountId={}", event.transactionId(), event.accountId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.TRANSACTION_CREATED_ROUTING_KEY, event);
    }
}
