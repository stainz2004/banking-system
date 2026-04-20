package org.example.tuum.dto;

import java.util.List;

public record AccountResponse(
        Long accountId,
        String customerId,
        List<BalanceResponse> balances
) {
}
