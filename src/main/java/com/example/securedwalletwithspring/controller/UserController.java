package com.example.securedwalletwithspring.controller;

import com.example.securedwalletwithspring.dto.EditedUserDto;
import com.example.securedwalletwithspring.dto.UserLoginDto;
import com.example.securedwalletwithspring.dto.UserRegistrationDto;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.exception.UserNotFoundException;
import com.example.securedwalletwithspring.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users/register")
    public ResponseEntity<Object> register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        Optional<User> oldUser = userService.getUserByNationalId(userRegistrationDto.getNationalId());
        if (oldUser.isEmpty()) {
            User user = userService.registerUser(userRegistrationDto);
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
            String token = userService.loginUser(userLoginDto);
            return ResponseEntity.ok(token);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with NationalId " + userLoginDto.getNationalId() + " not found");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUser(@Valid @RequestBody EditedUserDto editedUserDto) {
        User user = checkUserPresence(editedUserDto);
        return ResponseEntity.ok().body(user);
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
