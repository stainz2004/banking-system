package org.example.tuum.converter;

import org.example.tuum.dto.AccountResponse;
import org.example.tuum.dto.BalanceResponse;
import org.example.tuum.dto.CreateAccountRequest;
import org.example.tuum.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountConverter {
    @Mapping(target = "id", ignore = true)
    Account toEntity(CreateAccountRequest request);
    AccountResponse toResponse(Account account, List<BalanceResponse> balances);
}