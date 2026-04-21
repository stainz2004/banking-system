package org.example.tuum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tuum.dto.CreateAccountRequest;
import org.example.tuum.dto.AccountResponse;
import org.example.tuum.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }

    @GetMapping
    public ResponseEntity<AccountResponse> getAccount(@RequestParam Long customerId) {
        return ResponseEntity.ok(accountService.getAccountByAccountId(customerId));
    }
}
