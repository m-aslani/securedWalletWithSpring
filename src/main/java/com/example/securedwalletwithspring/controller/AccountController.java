package com.example.securedwalletwithspring.controller;

import com.example.securedwalletwithspring.dto.AccountDto;
import com.example.securedwalletwithspring.dto.EditedUserDto;
import com.example.securedwalletwithspring.entity.Account;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/add/account")
    public ResponseEntity<Account> addAccount(@Valid @RequestBody AccountDto accountDto) {
        return ResponseEntity.ok().body(accountService.addAccount(accountDto));
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(@Valid @RequestBody AccountDto accountDto) {
        accountService.deleteAccount(accountDto);
        return ResponseEntity.ok().body("Account deleted");
    }

}
