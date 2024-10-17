package com.example.securedwalletwithspring.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginDto {

    @NotBlank(message = "user national ID can NOT be Empty!")
    @Size(min = 10,max = 10,message = "Iranian national ID must include 10 digits!")
    private String nationalId;

    @NotBlank(message = "user password can NOT be Empty!")
    private String password;
}
