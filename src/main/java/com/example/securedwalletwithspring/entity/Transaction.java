package com.example.securedwalletwithspring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1 , message = "Transaction amount can be negative!")
    private double amount;

    @NotBlank(message = "Transaction type can not be empty!")
    private String transactionType;

    @NotNull(message = "Transaction time stamp can not be null!")
    private LocalDateTime timestamp;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

}
