package org.example.tuum.messaging;

import org.example.tuum.entity.Currency;

import java.math.BigDecimal;

public record BalanceUpdatedEvent(
        Long balanceId,
        Long accountId,
        Currency currency,
        BigDecimal newAmount
) {}
