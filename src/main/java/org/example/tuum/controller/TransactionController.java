package org.example.tuum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tuum.dto.CreateTransactionRequest;
import org.example.tuum.dto.CreateTransactionResponse;
import org.example.tuum.dto.TransactionsResponse;
import org.example.tuum.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Transactions", description = "Transaction management endpoints")
@RequestMapping("/api/transactions")
@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Get transactions by account ID")
    @GetMapping
    public ResponseEntity<List<TransactionsResponse>> getTransactionsByAccountId(@RequestParam Long accountId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.getTransactionsByAccountId(accountId));
    }

    @Operation(summary = "Create a new transaction")
    @PostMapping
    public ResponseEntity<CreateTransactionResponse> createTransaction(@RequestBody @Valid CreateTransactionRequest request) {
        return ResponseEntity.ok().body(transactionService.createTransaction(request));
    }
}
