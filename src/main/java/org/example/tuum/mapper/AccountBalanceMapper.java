package org.example.tuum.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.tuum.entity.AccountBalance;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountBalanceMapper {

    void insert(AccountBalance accountBalance);

    List<AccountBalance> findByAccountId(Long accountId);

    void updateAvailableAmount(@Param("id") Long id, @Param("availableAmount") BigDecimal availableAmount);
}
