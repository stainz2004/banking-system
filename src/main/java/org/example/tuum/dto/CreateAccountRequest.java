package org.example.tuum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.tuum.entity.Country;
import org.example.tuum.entity.Currency;

import java.util.List;

public record CreateAccountRequest(
        @NotBlank String customerId,
        @NotNull Country country,
        @NotEmpty List<Currency> currencies
)
{}

