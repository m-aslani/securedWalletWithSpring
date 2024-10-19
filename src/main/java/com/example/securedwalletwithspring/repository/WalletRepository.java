package com.example.securedwalletwithspring.repository;

import com.example.securedwalletwithspring.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
