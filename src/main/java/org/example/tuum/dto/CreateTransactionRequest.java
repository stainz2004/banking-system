package org.example.tuum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.tuum.entity.Currency;
import org.example.tuum.entity.Direction;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotNull Long accountId,
        @NotNull @Positive BigDecimal amount,
        @NotNull Currency currency,
        @NotNull Direction direction,
        @NotBlank String description
) {
}
