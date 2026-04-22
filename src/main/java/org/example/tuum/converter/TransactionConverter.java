package org.example.tuum.converter;

import org.example.tuum.dto.CreateTransactionRequest;
import org.example.tuum.dto.CreateTransactionResponse;
import org.example.tuum.dto.TransactionsResponse;
import org.example.tuum.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionConverter {

    @Mapping(target = "transactionId", source = "id")
    TransactionsResponse toTransactionResponse(Transaction transaction);

    List<TransactionsResponse> toTransactionsResponse(List<Transaction> transactions);


    @Mapping(target= "id", ignore = true)
    @Mapping(target = "balanceAfter", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Transaction toTransaction(CreateTransactionRequest transactionsRequest);

    @Mapping(target = "transactionId", source = "transaction.id")
    CreateTransactionResponse toCreateTransactionResponse(Transaction transaction);
}
