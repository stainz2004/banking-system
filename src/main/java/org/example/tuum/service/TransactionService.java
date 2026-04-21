package org.example.tuum.service;

import lombok.RequiredArgsConstructor;
import org.example.tuum.converter.TransactionConverter;
import org.example.tuum.dto.CreateTransactionRequest;
import org.example.tuum.dto.CreateTransactionResponse;
import org.example.tuum.dto.TransactionsResponse;
import org.example.tuum.entity.AccountBalance;
import org.example.tuum.entity.Currency;
import org.example.tuum.entity.Direction;
import org.example.tuum.entity.Transaction;
import org.example.tuum.exception.InsufficeientFundsException;
import org.example.tuum.mapper.AccountBalanceMapper;
import org.example.tuum.mapper.TransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionMapper transactionMapper;
    private final AccountBalanceMapper accountBalanceMapper;

    private final TransactionConverter transactionConverter;

    /**
     * Retrieves a list of transactions for a given account ID.
     *
     * @param accountId the ID of the account for which to retrieve transactions
     * @return a list of TransactionsResponse objects representing the transactions
     */
    @Transactional(readOnly = true)
    public List<TransactionsResponse> getTransactionsByAccountId(Long accountId) {
        List<Transaction> transactions = transactionMapper.findByAccountId(accountId);

        return transactionConverter.toTransactionsResponse(transactions);
    }

    /**
     * Creates a new transaction based on the provided request.
     *
     * @param request the CreateTransactionRequest containing transaction details
     * @return a CreateTransactionResponse representing the created transaction
     */
    @Transactional
    public CreateTransactionResponse createTransaction(CreateTransactionRequest request) {
        Long accountId = request.accountId();
        Currency currency = request.currency();

        AccountBalance balance = accountBalanceMapper.findByAccountIdAndCurrency(accountId, currency);

        if (balance == null) {
            throw new IllegalArgumentException("Account balance not found for account ID: " + accountId + " and currency: " + currency);
        }

        BigDecimal currentAmount = balance.getAvailableAmount();
        BigDecimal newBalance;

        if (request.direction() == Direction.IN) {
            newBalance = currentAmount.add(request.amount());
        } else {
            newBalance = currentAmount.subtract(request.amount());
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficeientFundsException("Not enough balance");
            }
        }

        accountBalanceMapper.updateAvailableAmount(balance.getId(), newBalance);

        Transaction transaction = transactionConverter.toTransaction(request);
        transaction.setBalanceAfter(newBalance);

        transactionMapper.insert(transaction);

        return transactionConverter.toCreateTransactionResponse(transaction);
    }
}
