package org.example.tuum.controller;

import org.example.tuum.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for TransactionController.
 *
 * <p>The database is wiped via TRUNCATE after each test
 */
@DisplayName("TransactionController Integration Tests")
class TransactionControllerIT extends BaseIntegrationTest {

    private long accountId;

    /**
     * Creates a fresh account with EUR and USD balances before every test.
     */
    @BeforeEach
    void setUp() throws Exception {
        String createAccountBody = """
                {
                    "customerId": "tx-customer-001",
                    "country": "EE",
                    "currencies": ["EUR", "USD"]
                }
                """;

        String response = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAccountBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        accountId = extractAccountId(response);
    }


    @Nested
    @DisplayName("POST /api/transactions – create transaction")
    class CreateTransaction {

        @Test
        @DisplayName("should create IN transaction and return 200 with updated balance")
        void createTransaction_inDirection_returns200() throws Exception {
            String requestBody = String.format("""
                    {
                        "accountId": %d,
                        "amount": 100.00,
                        "currency": "EUR",
                        "direction": "IN",
                        "description": "Cinema money"
                    }
                    """, accountId);

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountId").value(accountId))
                    .andExpect(jsonPath("$.transactionId").isNumber())
                    .andExpect(jsonPath("$.amount").value(100.00))
                    .andExpect(jsonPath("$.currency").value("EUR"))
                    .andExpect(jsonPath("$.direction").value("IN"))
                    .andExpect(jsonPath("$.description").value("Cinema money"))
                    .andExpect(jsonPath("$.balanceAfter").value(100.00));
        }

        @Test
        @DisplayName("should create OUT transaction after funding and return correct balance")
        void createTransaction_outDirection_returns200() throws Exception {
            String fundBody = String.format("""
                    {
                        "accountId": %d,
                        "amount": 200.00,
                        "currency": "EUR",
                        "direction": "IN",
                        "description": "Initial deposit on my house"
                    }
                    """, accountId);

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(fundBody))
                    .andExpect(status().isOk());

            String withdrawBody = String.format("""
                    {
                        "accountId": %d,
                        "amount": 50.00,
                        "currency": "EUR",
                        "direction": "OUT",
                        "description": "Kebab"
                    }
                    """, accountId);

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(withdrawBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountId").value(accountId))
                    .andExpect(jsonPath("$.direction").value("OUT"))
                    .andExpect(jsonPath("$.balanceAfter").value(150.00));
        }

        @Test
        @DisplayName("should return 409 when balance is insufficient for OUT transaction")
        void createTransaction_insufficientFunds_returns409() throws Exception {
            String requestBody = String.format("""
                    {
                        "accountId": %d,
                        "amount": 500.00,
                        "currency": "EUR",
                        "direction": "OUT",
                        "description": "Investing"
                    }
                    """, accountId);

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("should return 404 when wrong direction is used for transaction")
        void createTransaction_invalidDirection_returns404() throws Exception {
            String requestBody = String.format("""
                    {
                        "accountId": %d,
                        "amount": 500.00,
                        "currency": "EUR",
                        "direction": "UP",
                        "description": "Baltic stocks"
                    }
                    """, accountId);

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("should return 404 when account does not exist")
        void createTransaction_nonExistingAccount_returns404() throws Exception {
            String requestBody = """
                    {
                        "accountId": 999999,
                        "amount": 100.00,
                        "currency": "EUR",
                        "direction": "IN",
                        "description": "Money to an african prince"
                    }
                    """;

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("should return 400 when the deposited amount is invalid")
        void createTransaction_invalidAmount_returns400() throws Exception {
            String requestBody = String.format("""
                    {
                        "accountId": %d,
                        "amount": -500.00,
                        "currency": "EUR",
                        "direction": "IN",
                        "description": "Money that is coming to me"
                    }
                    """, accountId);

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }


        @Test
        @DisplayName("should return 400 when invalid description is provided")
        void createTransaction_invalidDescription_returns400() throws Exception {
            String requestBody = String.format("""
                    {
                        "accountId": %d,
                        "amount": 500.00,
                        "currency": "EUR",
                        "direction": "IN",
                        "description": ""
                    }
                    """, accountId);

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 404 when currency is not associated with the account")
        void createTransaction_currencyNotOnAccount_returns404() throws Exception {
            String requestBody = String.format("""
                    {
                        "accountId": %d,
                        "amount": 100.00,
                        "currency": "GBP",
                        "direction": "IN",
                        "description": "GBP deposit"
                    }
                    """, accountId);

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("should return 400 when amount is zero")
        void createTransaction_zeroAmount_returns400() throws Exception {
            String requestBody = String.format("""
                    {
                        "accountId": %d,
                        "amount": 0,
                        "currency": "EUR",
                        "direction": "IN",
                        "description": "Monthly payment"
                    }
                    """, accountId);

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when amount is negative")
        void createTransaction_negativeAmount_returns400() throws Exception {
            String requestBody = String.format("""
                    {
                        "accountId": %d,
                        "amount": -10.00,
                        "currency": "EUR",
                        "direction": "IN",
                        "description": "My last cents"
                    }
                    """, accountId);

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when description is blank")
        void createTransaction_blankDescription_returns400() throws Exception {
            String requestBody = String.format("""
                    {
                        "accountId": %d,
                        "amount": 100.00,
                        "currency": "EUR",
                        "direction": "IN",
                        "description": ""
                    }
                    """, accountId);

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.description").exists());
        }

        @Test
        @DisplayName("should return 400 when direction is invalid")
        void createTransaction_invalidDirection_returns400() throws Exception {
            String requestBody = String.format("""
                    {
                        "accountId": %d,
                        "amount": 100.00,
                        "currency": "EUR",
                        "direction": "INVALID",
                        "description": "Bad direction"
                    }
                    """, accountId);

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when currency is invalid")
        void createTransaction_invalidCurrency_returns400() throws Exception {
            String requestBody = String.format("""
                    {
                        "accountId": %d,
                        "amount": 100.00,
                        "currency": "XYZ",
                        "direction": "IN",
                        "description": "Bad currency"
                    }
                    """, accountId);

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when accountId is missing")
        void createTransaction_missingAccountId_returns400() throws Exception {
            String requestBody = """
                    {
                        "amount": 100.00,
                        "currency": "EUR",
                        "direction": "IN",
                        "description": "No account"
                    }
                    """;

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.accountId").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/transactions – get transactions by account ID")
    class GetTransactions {

        @Test
        @DisplayName("should return empty list when account has no transactions")
        void getTransactions_noTransactions_returnsEmptyList() throws Exception {
            mockMvc.perform(get("/api/transactions")
                            .param("accountId", String.valueOf(accountId)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("should return all transactions for the account")
        void getTransactions_withTransactions_returnsList() throws Exception {
            String tx1 = String.format("""
                    {
                        "accountId": %d,
                        "amount": 300.00,
                        "currency": "EUR",
                        "direction": "IN",
                        "description": "Deposit"
                    }
                    """, accountId);

            String tx2 = String.format("""
                    {
                        "accountId": %d,
                        "amount": 100.00,
                        "currency": "EUR",
                        "direction": "OUT",
                        "description": "Withdrawal"
                    }
                    """, accountId);

            mockMvc.perform(post("/api/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tx1)).andExpect(status().isOk());

            mockMvc.perform(post("/api/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tx2)).andExpect(status().isOk());

            mockMvc.perform(get("/api/transactions")
                            .param("accountId", String.valueOf(accountId)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].accountId", everyItem(is((int) accountId))))
                    .andExpect(jsonPath("$[*].currency", everyItem(is("EUR"))))
                    .andExpect(jsonPath("$[*].direction",
                            containsInAnyOrder("IN", "OUT")));
        }

        @Test
        @DisplayName("should return 404 when account does not exist")
        void getTransactions_nonExistingAccount_returns404() throws Exception {
            mockMvc.perform(get("/api/transactions")
                            .param("accountId", "999999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("should return 400 when accountId parameter is missing")
        void getTransactions_missingParam_returns400() throws Exception {
            mockMvc.perform(get("/api/transactions"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should only return transactions belonging to the requested account")
        void getTransactions_isolatedByAccount_returnsOnlyOwnTransactions() throws Exception {
            String secondAccountBody = """
                    {
                        "customerId": "tx-customer-002",
                        "country": "SE",
                        "currencies": ["EUR"]
                    }
                    """;

            String secondAccountResponse = mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(secondAccountBody))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            long secondAccountId = extractAccountId(secondAccountResponse);

            String tx = String.format("""
                    {
                        "accountId": %d,
                        "amount": 50.00,
                        "currency": "EUR",
                        "direction": "IN",
                        "description": "Other account deposit"
                    }
                    """, secondAccountId);

            mockMvc.perform(post("/api/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tx)).andExpect(status().isOk());

            mockMvc.perform(get("/api/transactions")
                            .param("accountId", String.valueOf(accountId)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    /**
     * Extracts the accountId field from a raw JSON string without
     */
    private long extractAccountId(String json) {
        String marker = "\"accountId\":";
        int start = json.indexOf(marker) + marker.length();
        int end = json.indexOf(',', start);
        return Long.parseLong(json.substring(start, end).trim());
    }
}
