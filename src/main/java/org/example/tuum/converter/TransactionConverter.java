package org.example.tuum.converter;

import org.example.tuum.dto.CreateTransactionRequest;
import org.example.tuum.dto.CreateTransactionResponse;
import org.example.tuum.dto.TransactionsResponse;
import org.example.tuum.entity.Transaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionConverter {

    List<TransactionsResponse> toTransactionsResponse(List<Transaction> transactions);

    Transaction toTransaction(CreateTransactionRequest transactionsRequest);

    CreateTransactionResponse toCreateTransactionResponse(Transaction transaction);
}
