package org.example.tuum.dto;

import org.example.tuum.entity.Currency;

import java.math.BigDecimal;

public record BalanceResponse(BigDecimal availableAmount, Currency currency) {}