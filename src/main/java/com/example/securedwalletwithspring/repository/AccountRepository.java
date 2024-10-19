package com.example.securedwalletwithspring.repository;

import com.example.securedwalletwithspring.entity.Account;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByAccountIban(String accountIban);

    List<Account> findByWallet(Wallet wallet);
//    Optional<Account> findByUser(User user);
}
