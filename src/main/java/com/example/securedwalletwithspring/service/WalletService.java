package com.example.securedwalletwithspring.service;

import com.example.securedwalletwithspring.dto.UserLoginDto;
import com.example.securedwalletwithspring.dto.UserRegistrationDto;
import com.example.securedwalletwithspring.dto.WalletDto;
import com.example.securedwalletwithspring.entity.Account;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.entity.Wallet;
import com.example.securedwalletwithspring.exception.AccountNotFoundException;
import com.example.securedwalletwithspring.exception.UserNotFoundException;
import com.example.securedwalletwithspring.repository.AccountRepository;
import com.example.securedwalletwithspring.repository.UserRepository;
import com.example.securedwalletwithspring.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WalletRepository walletRepository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JwtService jwtService;
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Autowired
//    private AccountService accountService;

    @Autowired
    private UserService userService;

    public Wallet createWallet(WalletDto walletDto) {
        Optional<User> user = userService.getUserByNationalId(walletDto.getNationalId());
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with " +walletDto.getNationalId() + " not found");
        }

        if(user.get().getWallet() != null) {
            throw new UserNotFoundException("wallet for user with " +walletDto.getNationalId() + " already exists");
        }

        Wallet wallet = new Wallet();
        wallet.setUser(user.get());

        walletRepository.save(wallet);

        user.get().setWallet(wallet);
        userRepository.save(user.get());

        return wallet;
    }


    //update total balance of wallet
    public void updateTotalWalletBalance(Wallet wallet) {
        List<Account> accounts = accountRepository.findByWallet(wallet);
        double totalBalance = accounts.stream().mapToDouble(Account::getAccountBalance).sum();
        wallet.setTotalBalance(totalBalance);
        walletRepository.save(wallet);
    }

    //find wallet by user
    public Wallet getWalletByUser(String nationalId) {
        System.out.println("2222222222222222222");
        Optional<User> user = userRepository.findByNationalId(nationalId);
        if(user.isPresent()) {
            System.out.println("33333333333333333333");
            return user.get().getWallet();
        }
        throw new AccountNotFoundException("Account number for: "+nationalId + " does not exist");
    }


}
