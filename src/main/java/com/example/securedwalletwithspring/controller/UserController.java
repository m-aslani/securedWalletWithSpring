package com.example.securedwalletwithspring.controller;

import com.example.securedwalletwithspring.dto.EditedUserDto;
import com.example.securedwalletwithspring.dto.UserLoginDto;
import com.example.securedwalletwithspring.dto.UserRegistrationDto;
import com.example.securedwalletwithspring.entity.Account;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.exception.UserNotFoundException;
import com.example.securedwalletwithspring.service.AccountService;
import com.example.securedwalletwithspring.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/user")
    public ResponseEntity<List> getUser(@Valid @RequestBody EditedUserDto editedUserDto) {
        User user = checkUserPresence(editedUserDto);
        List<Account> accounts = accountService.findAccountByWallet(user.getWallet());
        List<Object> result = List.of(user,accounts);
        return ResponseEntity.ok().body(result);
    }

    @PutMapping("/update/phoneNumber")
    public ResponseEntity<String> updatePhoneNumber(@Valid @RequestBody EditedUserDto editedUserDto) {
        User user = checkUserPresence(editedUserDto);
        String phoneNumber = userService.updateUserPhoneNumber(editedUserDto, user);
        return ResponseEntity.ok().body("Your phone number changed to : "+phoneNumber);
    }

    @PutMapping("/update/email")
    public ResponseEntity<String> updateEmail(@Valid @RequestBody EditedUserDto editedUserDto) {
        User user = checkUserPresence(editedUserDto);
        String email = userService.updateUserEmail(editedUserDto, user);
        return ResponseEntity.ok().body("Your phone number changed to : "+email);
    }

    @PutMapping("/update/password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody EditedUserDto editedUserDto) {
        User user = checkUserPresence(editedUserDto);
        String password = userService.updateUserPassword(editedUserDto , user);
        return ResponseEntity.ok().body("Your phone number changed to : "+password);
    }

    private User checkUserPresence(EditedUserDto editedUserDto) {
        Optional<User> user = userService.getUserByNationalId(editedUserDto.getNationalId());
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with National ID " + editedUserDto.getNationalId() + " not found");
        }
        return user.get();
    }



}
