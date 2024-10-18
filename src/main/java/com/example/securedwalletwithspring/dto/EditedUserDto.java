package com.example.securedwalletwithspring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditedUserDto {

    @NotBlank(message = "user national ID can NOT be Empty!")
    @Size(min = 10,max = 10,message = "Iranian national ID must include 10 digits!")
    private String nationalId;

    private String password;

    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$" , message = "E-mail is not valid!")
    private String email;

    @Pattern(regexp = "^(\\+98|0)?9\\d{9}$" , message = "phone number is not valid!")
    private String phoneNumber;
}
