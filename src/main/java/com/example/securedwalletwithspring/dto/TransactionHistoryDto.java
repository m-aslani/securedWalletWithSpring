package com.example.securedwalletwithspring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryDto {

    @NotNull(message = "account number can not be null")
    private String accountNumber;

    @NotBlank(message = "Sender National ID can not be Empty!")
    @Size(min = 10,max = 10,message = "Iranian national ID must include 10 digits!")
    private String senderNationalID;
}
