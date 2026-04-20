package org.example.tuum.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountBalance {
    private Long id;
    private Long accountId;
    private Currency currency;
    private BigDecimal availableAmount;
}
