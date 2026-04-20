package org.example.tuum.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.tuum.entity.Transaction;

import java.util.List;

@Mapper
public interface TransactionMapper {

    void insert(Transaction transaction);

    List<Transaction> findByAccountId(Long accountId);
}
