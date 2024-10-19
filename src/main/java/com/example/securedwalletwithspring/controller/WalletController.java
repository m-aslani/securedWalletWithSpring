package com.example.securedwalletwithspring.controller;

import com.example.securedwalletwithspring.dto.UserLoginDto;
import com.example.securedwalletwithspring.dto.UserRegistrationDto;
import com.example.securedwalletwithspring.entity.User;
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

    @Autowired
    private UserService userService;

    @PostMapping("/users/register")
    public ResponseEntity<Object> register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        Optional<User> oldUser = userService.getUserByNationalId(userRegistrationDto.getNationalId());
        if (oldUser.isEmpty()) {
            User user = walletService.registerUser(userRegistrationDto);
            return ResponseEntity.ok(user);
        }
        else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User with NationalId " + userRegistrationDto.getNationalId() + " already exists");
        }
    }

    @PostMapping("/users/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        Optional<User> user = userService.getUserByNationalId(userLoginDto.getNationalId());
        if (user.isPresent()) {
            String token = walletService.loginUser(userLoginDto);
            return ResponseEntity.ok(token);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with NationalId " + userLoginDto.getNationalId() + " not found");
        }
    }
}
