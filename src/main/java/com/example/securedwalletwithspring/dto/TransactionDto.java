package com.example.securedwalletwithspring.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    @NotNull(message = "Transaction amount can NOT be empty!")
    @Min(value = 1 , message = "Transaction amount must be greater than 1$!")
    private double amount;

    @NotBlank(message = "Transaction type can not be empty!")
    private String transactionType;

    @NotBlank(message = "Sender National ID can not be Empty!")
    private String senderNationalID;

    @NotBlank(message = "sender account number can not be empty!")
    private String sender;

    @NotBlank(message = "receiver account number can not be empty!")
    private String receiver;
}
