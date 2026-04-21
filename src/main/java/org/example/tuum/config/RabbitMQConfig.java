package org.example.tuum.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "banking.exchange";

    public static final String ACCOUNT_CREATED_ROUTING_KEY = "account.created";
    public static final String BALANCE_CREATED_ROUTING_KEY = "balance.created";
    public static final String BALANCE_UPDATED_ROUTING_KEY = "balance.updated";
    public static final String TRANSACTION_CREATED_ROUTING_KEY = "transaction.created";

    public static final String ACCOUNT_CREATED_QUEUE = "account.created.queue";
    public static final String BALANCE_CREATED_QUEUE = "balance.created.queue";
    public static final String BALANCE_UPDATED_QUEUE = "balance.updated.queue";
    public static final String TRANSACTION_CREATED_QUEUE = "transaction.created.queue";

    @Bean
    public DirectExchange bankingExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue accountCreatedQueue() {
        return new Queue(ACCOUNT_CREATED_QUEUE, true);
    }

    @Bean
    public Queue balanceCreatedQueue() {
        return new Queue(BALANCE_CREATED_QUEUE, true);
    }

    @Bean
    public Queue balanceUpdatedQueue() {
        return new Queue(BALANCE_UPDATED_QUEUE, true);
    }

    @Bean
    public Queue transactionCreatedQueue() {
        return new Queue(TRANSACTION_CREATED_QUEUE, true);
    }

    @Bean
    public Binding accountCreatedBinding(Queue accountCreatedQueue, DirectExchange bankingExchange) {
        return BindingBuilder.bind(accountCreatedQueue).to(bankingExchange).with(ACCOUNT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding balanceCreatedBinding(Queue balanceCreatedQueue, DirectExchange bankingExchange) {
        return BindingBuilder.bind(balanceCreatedQueue).to(bankingExchange).with(BALANCE_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding balanceUpdatedBinding(Queue balanceUpdatedQueue, DirectExchange bankingExchange) {
        return BindingBuilder.bind(balanceUpdatedQueue).to(bankingExchange).with(BALANCE_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding transactionCreatedBinding(Queue transactionCreatedQueue, DirectExchange bankingExchange) {
        return BindingBuilder.bind(transactionCreatedQueue).to(bankingExchange).with(TRANSACTION_CREATED_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         JacksonJsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
