package org.example.tuum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tuum.dto.CreateTransactionRequest;
import org.example.tuum.dto.CreateTransactionResponse;
import org.example.tuum.dto.TransactionsResponse;
import org.example.tuum.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/transactions")
@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionsResponse>> getTransactionsByAccountId(@RequestParam Long accountId) {
        return ResponseEntity.ok().body(transactionService.getTransactionsByAccountId(accountId));
    }

    @PostMapping
    public ResponseEntity<CreateTransactionResponse> createTransaction(@RequestBody @Valid CreateTransactionRequest request) {
        return ResponseEntity.ok().body(transactionService.createTransaction(request));
    }
}
