package com.example.securedwalletwithspring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {

    @NotBlank(message = "user national ID can NOT be Empty!")
    private String nationalId;

    @NotBlank(message = "user first name can NOT be Empty!")
    private String firstname;

    @NotBlank(message = "user last name can NOT be Empty!")
    private String lastname;

    @NotBlank(message = "user password can NOT be Empty!")
    private String password;

    @NotBlank(message = "user email can NOT be Empty!")
    @Email
    private String email;

    @NotBlank(message = "user phone number can NOT be Empty!")
    private String phoneNumber;

    @NotBlank(message = "user birth date can NOT be Empty!")
    private String birthDate;

    @NotBlank(message = "user gender can NOT be Empty!")
    private String gender;

    @NotNull(message = "user militaryStatus can NOT be Empty!")
    private boolean militaryStatus;

    @NotNull
    @Min(value = 1 ,message = "Initial Amount can Not be null!")
    private double initialAmount;

}
