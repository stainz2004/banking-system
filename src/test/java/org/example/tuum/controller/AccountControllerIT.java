package org.example.tuum.controller;

import org.example.tuum.BaseIntegrationTest;
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
 * Integration tests for AccountController.
 *
 * <p>The database is wiped via TRUNCATE after each test, so each test
 * starts with a clean slate.
 */
@DisplayName("AccountController Integration Tests")
class AccountControllerIT extends BaseIntegrationTest {

    @Nested
    @DisplayName("POST /api/accounts – create account")
    class CreateAccount {

        @Test
        @DisplayName("should create account with single currency and return 201")
        void createAccount_singleCurrency_returns201() throws Exception {
            String requestBody = """
                    {
                        "customerId": "customer-001",
                        "country": "EE",
                        "currencies": ["EUR"]
                    }
                    """;

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accountId").isNumber())
                    .andExpect(jsonPath("$.customerId").value("customer-001"))
                    .andExpect(jsonPath("$.balances", hasSize(1)))
                    .andExpect(jsonPath("$.balances[0].currency").value("EUR"))
                    .andExpect(jsonPath("$.balances[0].availableAmount").value(0));
        }

        @Test
        @DisplayName("should create account with multiple currencies and return 201")
        void createAccount_multipleCurrencies_returns201() throws Exception {
            String requestBody = """
                    {
                        "customerId": "customer-002",
                        "country": "SE",
                        "currencies": ["EUR", "SEK", "USD"]
                    }
                    """;

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accountId").isNumber())
                    .andExpect(jsonPath("$.customerId").value("customer-002"))
                    .andExpect(jsonPath("$.balances", hasSize(3)))
                    .andExpect(jsonPath("$.balances[*].currency",
                            containsInAnyOrder("EUR", "SEK", "USD")))
                    .andExpect(jsonPath("$.balances[*].availableAmount",
                            everyItem(is(0))));
        }

        @Test
        @DisplayName("should return 400 when customerId is blank")
        void createAccount_blankCustomerId_returns400() throws Exception {
            String requestBody = """
                    {
                        "customerId": "",
                        "country": "EE",
                        "currencies": ["EUR"]
                    }
                    """;

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.customerId").exists());
        }

        @Test
        @DisplayName("should return 400 when currencies list is empty")
        void createAccount_emptyCurrencies_returns400() throws Exception {
            String requestBody = """
                    {
                        "customerId": "customer-003",
                        "country": "EE",
                        "currencies": []
                    }
                    """;

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.currencies").exists());
        }

        @Test
        @DisplayName("should return 400 when country is null")
        void createAccount_nullCountry_returns400() throws Exception {
            String requestBody = """
                    {
                        "customerId": "customer-004",
                        "currencies": ["EUR"]
                    }
                    """;

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.country").exists());
        }

        @Test
        @DisplayName("should return 400 when currency value is invalid")
        void createAccount_invalidCurrency_returns400() throws Exception {
            String requestBody = """
                    {
                        "customerId": "customer-005",
                        "country": "EE",
                        "currencies": ["NOTREAL"]
                    }
                    """;

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when country value is invalid")
        void createAccount_invalidCountry_returns400() throws Exception {
            String requestBody = """
                    {
                        "customerId": "customer-006",
                        "country": "TSE",
                        "currencies": ["EUR"]
                    }
                    """;

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when request body is missing")
        void createAccount_missingBody_returns400() throws Exception {
            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/accounts/{accountId} – get account by ID")
    class GetAccount {

        @Test
        @DisplayName("should return account when it exists")
        void getAccount_existingId_returns200() throws Exception {
            // First create an account
            String createBody = """
                    {
                        "customerId": "customer-001",
                        "country": "GB",
                        "currencies": ["GBP", "EUR"]
                    }
                    """;

            String createResponse = mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createBody))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            long accountId = extractAccountId(createResponse);

            mockMvc.perform(get("/api/accounts/{accountId}", accountId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountId").value(accountId))
                    .andExpect(jsonPath("$.customerId").value("customer-001"))
                    .andExpect(jsonPath("$.balances", hasSize(2)))
                    .andExpect(jsonPath("$.balances[*].currency",
                            containsInAnyOrder("GBP", "EUR")));
        }

        @Test
        @DisplayName("should return 404 when account does not exist")
        void getAccount_nonExistingId_returns404() throws Exception {
            mockMvc.perform(get("/api/accounts/{accountId}", 999999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists());
        }
    }
}
