package com.example.securedwalletwithspring.controller;

import com.example.securedwalletwithspring.dto.UserLoginDto;
import com.example.securedwalletwithspring.dto.UserRegistrationDto;
import com.example.securedwalletwithspring.dto.WalletDto;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.entity.Wallet;
import com.example.securedwalletwithspring.service.UserService;
import com.example.securedwalletwithspring.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("/wallet/create")
    public ResponseEntity<Wallet> createWallet(@RequestBody @Valid WalletDto walletDto) {
        Wallet wallet = walletService.createWallet(walletDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }

}
