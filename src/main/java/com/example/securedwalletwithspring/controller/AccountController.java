package com.example.securedwalletwithspring.controller;

import com.example.securedwalletwithspring.dto.EditedUserDto;
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

    @GetMapping("/user")
    public ResponseEntity<User> getUser(@Valid @RequestBody EditedUserDto editedUserDto) {
        Optional<User> user = accountService.findUserByNationalId(editedUserDto);
      return ResponseEntity.ok().body(user.get());
    }

    @PutMapping("/update/phoneNumber")
    public ResponseEntity<String> updatePhoneNumber(@Valid @RequestBody EditedUserDto editedUserDto) {
        String phoneNumber = accountService.updateUserPhoneNumber(editedUserDto);
        return ResponseEntity.ok().body("Your phone number changed to : "+phoneNumber);
    }

    @PutMapping("/update/email")
    public ResponseEntity<String> updateEmail(@Valid @RequestBody EditedUserDto editedUserDto) {
        String email = accountService.updateUserPhoneNumber(editedUserDto);
        return ResponseEntity.ok().body("Your phone number changed to : "+email);
    }

    @PutMapping("/update/password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody EditedUserDto editedUserDto) {
        String password = accountService.updateUserPhoneNumber(editedUserDto);
        return ResponseEntity.ok().body("Your phone number changed to : "+password);
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(@Valid @RequestBody EditedUserDto editedUserDto) {
        accountService.deleteUser(editedUserDto);
        return ResponseEntity.ok().body("Account deleted");
    }

}
