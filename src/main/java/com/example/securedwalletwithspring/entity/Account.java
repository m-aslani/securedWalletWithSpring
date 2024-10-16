package com.example.securedwalletwithspring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0 , message = "Account Balance can NOT be negative!")
    private double accountBalance;

    @NotBlank(message = "Account Number can NOT be empty!")
    @Size(min = 16 , max = 16 , message = "Account Number must have 16 digits!")
    private String accountNumber;

    @NotNull(message = "Account open date can NOT be empty!")
    private LocalDate createdAt;

    @NotBlank(message = "Account IBAN can NOT be empty!")
    @Size(min = 24 , max = 24 , message = "IBAN must have 24 digits!")
    private String accountIban;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Generates 16 random digits to create account Number.
    public String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNum = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            accountNum.append(random.nextInt(10));
        }
        return accountNum.toString();
    }

    // Generates 16 random digits to create account IBAN.
    public String generateAccountIban() {
        Random random = new Random();
        StringBuilder accountIban = new StringBuilder();
        accountIban.append("IR");
        for (int i = 0; i < 22; i++) {
            accountIban.append(random.nextInt(10));
        }
        return accountIban.toString();
    }
}
