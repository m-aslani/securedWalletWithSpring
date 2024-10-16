package com.example.securedwalletwithspring.controller;

import com.example.securedwalletwithspring.dto.TransactionDto;
import com.example.securedwalletwithspring.dto.TransactionHistoryDto;
import com.example.securedwalletwithspring.entity.Transaction;
import com.example.securedwalletwithspring.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transaction/add-money")
    public ResponseEntity<String> addMoney(@Valid @RequestBody TransactionDto transactionDto) {
        transactionService.addMoney(transactionDto);
        return ResponseEntity.ok("Transaction added");
    }

    @PostMapping("/transaction/transfer-money")
    public ResponseEntity<String> transferMoney(@Valid @RequestBody TransactionDto transactionDto) {
        transactionService.transferMoney(transactionDto);
        return ResponseEntity.ok("Transaction transferred");
    }

    @GetMapping("/transaction")
    public ResponseEntity<List<Transaction>> getTransactions(@Valid @RequestBody TransactionHistoryDto transactionHistoryDto) {
        List<Transaction> transactions = transactionService.getTransactionHistory(transactionHistoryDto);
        return ResponseEntity.ok(transactions);
    }

}
