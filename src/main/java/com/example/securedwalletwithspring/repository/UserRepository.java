package com.example.securedwalletwithspring.repository;

import com.example.securedwalletwithspring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
//    User findByUsername(String username);
    Optional<User> findByNationalId(String nationalId);
}
