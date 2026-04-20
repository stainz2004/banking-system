package org.example.tuum.converter;

import org.example.tuum.dto.BalanceResponse;
import org.example.tuum.entity.AccountBalance;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountBalanceConverter {

    List<BalanceResponse> toResponse(List<AccountBalance> accountBalances);
}
