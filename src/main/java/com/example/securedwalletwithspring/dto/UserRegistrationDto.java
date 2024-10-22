package com.example.securedwalletwithspring.dto;

import com.example.securedwalletwithspring.entity.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {

    @NotBlank(message = "user national ID can NOT be Empty!")
    @Size(min = 10,max = 10,message = "Iranian national ID must include 10 digits!")
    private String nationalId;

    @NotBlank(message = "user first name can NOT be Empty!")
    private String firstname;

    @NotBlank(message = "user last name can NOT be Empty!")
    private String lastname;

    @NotBlank(message = "user password can NOT be Empty!")
    private String password;

    @NotBlank(message = "user email can NOT be Empty!")
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$" , message = "E-mail is not valid!")
    private String email;

    @NotBlank(message = "user phone number can NOT be Empty!")
    @Pattern(regexp = "^(\\+98|0)?9\\d{9}$" , message = "phone number is not valid!")
    private String phoneNumber;

    @NotBlank(message = "user birth date can NOT be Empty!")
    private String birthDate;

    @NotNull(message = "user gender can NOT be Empty!")
    private Gender gender;

    @NotNull(message = "user militaryStatus can NOT be Empty!")
    private boolean militaryStatus;

//    @NotNull
//    @Min(value = 1 ,message = "Initial Amount can Not be null!")
//    private double initialAmount;

}
