package org.example.tuum.service;

import lombok.RequiredArgsConstructor;
import org.example.tuum.converter.AccountBalanceConverter;
import org.example.tuum.converter.AccountConverter;
import org.example.tuum.dto.BalanceResponse;
import org.example.tuum.dto.CreateAccountRequest;
import org.example.tuum.dto.AccountResponse;
import org.example.tuum.entity.Account;
import org.example.tuum.entity.AccountBalance;
import org.example.tuum.exception.AccountNotFoundException;
import org.example.tuum.mapper.AccountBalanceMapper;
import org.example.tuum.mapper.AccountMapper;
import org.example.tuum.messaging.AccountBalanceCreatedEvent;
import org.example.tuum.messaging.AccountCreatedEvent;
import org.example.tuum.messaging.RabbitMQPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper accountMapper;
    private final AccountBalanceMapper accountBalanceMapper;

    private final AccountConverter accountConverter;
    private final AccountBalanceConverter accountBalanceConverter;

    private final RabbitMQPublisher rabbitMQPublisher;

    /**
     * Creates a new account based on the provided request.
     *
     * @param request The request containing customer ID and initial currencies.
     * @return An AccountResponse containing the details of the created account and its associated balances.
     */
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        Account account = accountConverter.toEntity(request);

        accountMapper.insert(account);

        rabbitMQPublisher.publishAccountCreated(
                new AccountCreatedEvent(account.getId(), account.getCustomerId(), account.getCountry())
        );

        Long accountId = account.getId();

        List<BalanceResponse> balances = request.currencies().stream()
                .map(currency -> {
                    AccountBalance balance = new AccountBalance();
                    balance.setAccountId(accountId);
                    balance.setCurrency(currency);
                    balance.setAvailableAmount(BigDecimal.ZERO);
                    accountBalanceMapper.insert(balance);

                    rabbitMQPublisher.publishAccountBalanceCreated(
                            new AccountBalanceCreatedEvent(balance.getId(), accountId, currency, BigDecimal.ZERO)
                    );

                    return new BalanceResponse(BigDecimal.ZERO, currency);
                })
                .toList();

        return new AccountResponse(account.getId(), account.getCustomerId(), balances);
    }

    /**
     * Retrieves account details along with balances for a given account ID.
     *
     * @param accountId The ID of the account to retrieve.
     * @return An AccountResponse containing account details and associated balances.
     */
    @Transactional(readOnly = true)
    public AccountResponse getAccountByAccountId(Long accountId) {
        Account account = accountMapper.findById(accountId);

        if (account == null) {
            throw new AccountNotFoundException("Account not found for account ID: " + accountId);
        }

        List<AccountBalance> balances = accountBalanceMapper.findByAccountId(accountId);
        List<BalanceResponse> balanceResponses = accountBalanceConverter.toResponse(balances);

        return accountConverter.toResponse(account, balanceResponses);
    }
}
