package org.example.tuum.messaging;

import org.example.tuum.entity.Currency;

import java.math.BigDecimal;

public record AccountBalanceCreatedEvent(
        Long balanceId,
        Long accountId,
        Currency currency,
        BigDecimal availableAmount
) {}
