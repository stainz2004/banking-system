package org.example.tuum.dto;

import org.example.tuum.entity.Currency;
import org.example.tuum.entity.Direction;

import java.math.BigDecimal;

public record TransactionsResponse(
        Long accountId,
        Long transactionId,
        BigDecimal amount,
        Currency currency,
        Direction direction,
        String description
) {
}
