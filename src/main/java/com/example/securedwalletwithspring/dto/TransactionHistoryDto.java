package com.example.securedwalletwithspring.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryDto {

    @NotNull(message = "account number can not be null")
    private String accountNumber;
}
