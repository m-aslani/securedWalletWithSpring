package com.example.securedwalletwithspring.service;

import com.example.securedwalletwithspring.dto.EditedUserDto;
import com.example.securedwalletwithspring.dto.UserLoginDto;
import com.example.securedwalletwithspring.dto.UserRegistrationDto;
import com.example.securedwalletwithspring.entity.Account;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.exception.UserNotFoundException;
import com.example.securedwalletwithspring.repository.AccountRepository;
import com.example.securedwalletwithspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Optional<User> getUserByNationalId(String nationalId) {
        return userRepository.findByNationalId(nationalId);
    }

    //update phone number
    public String updateUserPhoneNumber(EditedUserDto editedUserDto , User user){
        user.setPhoneNumber(editedUserDto.getPhoneNumber());
        userRepository.save(user);
        return editedUserDto.getPhoneNumber();
    }

    //update email
    public String updateUserEmail(EditedUserDto editedUserDto , User user){
        user.setEmail(editedUserDto.getEmail());
        userRepository.save(user);
        return editedUserDto.getEmail();
    }

    //update password
    public String updateUserPassword(EditedUserDto editedUserDto , User user){
        user.setPassword(passwordEncoder.encode(editedUserDto.getPassword()));
        userRepository.save(user);
        return editedUserDto.getPassword();
    }

}
