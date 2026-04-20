package org.example.tuum.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.tuum.entity.Account;

import java.util.List;

@Mapper
public interface AccountMapper {

    void insert(Account account);

    Account findById(Long id);

    List<Account> findAll();
}
