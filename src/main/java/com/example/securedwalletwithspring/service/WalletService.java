package com.example.securedwalletwithspring.service;

import com.example.securedwalletwithspring.dto.UserLoginDto;
import com.example.securedwalletwithspring.dto.UserRegistrationDto;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AccountService accountService;

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

        boolean validUser = user.checkMilitaryStatus(user.getBirthDate() , user.getGender() , user.isMilitaryStatus());

        if(!validUser) {
            Wallet wallet = new Wallet();
            wallet.setUser(user);

            user.setWallet(wallet);

            Account account = accountService.createAccount(userRegistrationDto.getInitialAmount());

            account.setWallet(wallet);

            wallet.setTotalBalance(account.getAccountBalance());

            userRepository.save(user);
            walletRepository.save(wallet);

            accountRepository.save(account);

            }else {
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
