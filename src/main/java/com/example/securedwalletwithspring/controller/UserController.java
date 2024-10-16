package com.example.securedwalletwithspring.controller;

import com.example.securedwalletwithspring.dto.UserLoginDto;
import com.example.securedwalletwithspring.dto.UserRegistrationDto;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        User user = userService.registerUser(userRegistrationDto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        Optional<User> user = userService.getUserByNationalId(userLoginDto.getNationalId());
        if (user.isPresent()) {
            String token = userService.loginUser(userLoginDto);
            return ResponseEntity.ok(token);
        }
        else {
            return ResponseEntity.status(401).body("Not Registered");
        }

    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
