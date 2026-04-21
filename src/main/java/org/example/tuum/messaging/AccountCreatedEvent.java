package org.example.tuum.messaging;

import org.example.tuum.entity.Country;

public record AccountCreatedEvent(
        Long accountId,
        String customerId,
        Country country
) {}
