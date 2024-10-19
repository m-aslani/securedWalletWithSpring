package com.example.securedwalletwithspring.service;

import com.example.securedwalletwithspring.dto.AccountDto;
import com.example.securedwalletwithspring.dto.EditedUserDto;
import com.example.securedwalletwithspring.entity.Account;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.entity.Wallet;
import com.example.securedwalletwithspring.exception.AccountNotFoundException;
import com.example.securedwalletwithspring.exception.UserNotFoundException;
import com.example.securedwalletwithspring.repository.AccountRepository;
import com.example.securedwalletwithspring.repository.UserRepository;
import com.example.securedwalletwithspring.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletRepository walletRepository;

    public List<Account> findAccountByWallet(Wallet wallet){
        return accountRepository.findByWallet(wallet);
    }

    //add new account to wallet
    public Account addAccount(AccountDto accountDto){
        Optional<User> user = userService.getUserByNationalId(accountDto.getNationalId());
        if(user.isEmpty()){
            throw new UserNotFoundException("User with National ID " + accountDto.getNationalId() + " not found");
        }
        Account account = createAccount(accountDto.getInitialAmount());
        System.out.println("Account created: " + account.getAccountBalance());
        account.setWallet(user.get().getWallet());
        accountRepository.save(account);
        user.get().getWallet().setTotalBalance(user.get().getWallet().getTotalBalance() + account.getAccountBalance());
        walletRepository.save(user.get().getWallet());
        return account ;
    }

    public Account findAccountByAccountNumber(String accountNumber){
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if (account.isEmpty()){
            throw new AccountNotFoundException("Account with " + accountNumber + " not found");
        }
        return account.get();
    }

    //delete user account (soft delete)
    public void deleteAccount(AccountDto accountDto){
        Optional<User> user = userService.getUserByNationalId(accountDto.getNationalId());
        if(user.isEmpty()){
            throw new UserNotFoundException("User with National ID " + accountDto.getNationalId() + " not found");
        }
        Account account = findAccountByAccountNumber(accountDto.getAccountNumber());
        account.setActive(false);
        accountRepository.save(account);
    }

    public Account createAccount(Double initialAmount) {
        if(initialAmount < 10) {
            throw new UserNotFoundException("Initial amount must be greater than 10$");
        }
        Account account = new Account();
        account.setCreatedAt(LocalDate.now());
        account.setAccountBalance(initialAmount);

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

        return account;
    }


}
