package com.example.securedwalletwithspring.service;

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
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    public User registerUser(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        user.setNationalId(userRegistrationDto.getNationalId());
        user.setFirstname(userRegistrationDto.getFirstname());
        user.setLastname(userRegistrationDto.getLastname());
        user.setEmail(userRegistrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setPhoneNumber(userRegistrationDto.getPhoneNumber());
        user.setBirthDate(userRegistrationDto.getBirthDate());
        user.setGender(userRegistrationDto.getGender());
        user.setMilitaryStatus(userRegistrationDto.isMilitaryStatus());

        boolean validAccount = user.checkMilitaryStatus(user.getBirthDate() , user.getGender() , user.isMilitaryStatus());

        if(!validAccount) {
        if(userRegistrationDto.getInitialAmount() > 10) {
            Account account = new Account();
            account.setCreatedAt(LocalDate.now());
            account.setAccountBalance(userRegistrationDto.getInitialAmount());
            while (true){
                String accountNumber = account.generateAccountNumber();
                if(accountRepository.findByAccountNumber(accountNumber).isEmpty()) {
                    account.setAccountNumber(accountNumber);
                    break;
                }
            }
            while (true){
                String accountIban = account.generateAccountIban();
                if(accountRepository.findByAccountIban(accountIban).isEmpty()) {
                    account.setAccountIban(accountIban);
                    break;
                }
            }
            account.setUser(user);
            user.setAccount(account);
            userRepository.save(user);
            accountRepository.save(account);
        }else {
            throw new UserNotFoundException("Initial amount must be greater than 10$");
        }}else {
            throw new UserNotFoundException("you can not create account due to your military status");
        }

//        System.out.println(user.getNationalId());
        return user;
    }

    public String loginUser(UserLoginDto userLoginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginDto.getNationalId(), userLoginDto.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(userLoginDto.getNationalId());
        return jwtService.generateToken(userDetails);
    }

    public Optional<User> getUserByNationalId(String nationalId) {
        return userRepository.findByNationalId(nationalId);
    }

}
