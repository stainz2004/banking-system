package org.example.tuum.messaging;

import org.example.tuum.entity.Currency;
import org.example.tuum.entity.Direction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionCreatedEvent(
        Long transactionId,
        Long accountId,
        BigDecimal amount,
        Currency currency,
        Direction direction,
        String description,
        BigDecimal balanceAfter,
        Instant createdAt
) {}
