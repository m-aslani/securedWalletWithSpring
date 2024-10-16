package com.example.securedwalletwithspring.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    @NotBlank(message = "Transaction amount can NOT be empty!")
    @Min(value = 1 , message = "Transaction amount must be greater than 1$!")
    private double amount;
}
