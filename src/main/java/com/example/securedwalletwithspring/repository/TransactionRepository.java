package com.example.securedwalletwithspring.repository;

import com.example.securedwalletwithspring.entity.Account;
import com.example.securedwalletwithspring.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByAccount(Account account);
    List<Transaction> findByTimestampBetween(LocalDateTime from, LocalDateTime to);
}
