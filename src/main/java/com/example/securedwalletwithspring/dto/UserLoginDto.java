package com.example.securedwalletwithspring.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {

    @NotBlank(message = "user national ID can NOT be Empty!")
    private String nationalId;

    @NotBlank(message = "user password can NOT be Empty!")
    private String password;
}
