package com.example.securedwalletwithspring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "my_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Pattern(regexp = "^(\\+98|0)?9\\d{9}$" , message = "phone number is not valid!")
    private String phoneNumber;

    @NotBlank(message = "user birth date can NOT be Empty!")
    private String birthDate;

    @NotBlank(message = "user gender can NOT be Empty!")
    private String gender;

    @NotNull(message = "user militaryStatus can NOT be Empty!")
    private boolean militaryStatus;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;

    public boolean checkMilitaryStatus(String birthDate , String gender , boolean militaryStatus) {
        if(gender.equals("male") && !militaryStatus) {
            String[] date = birthDate.split("/");
            int year = Integer.parseInt(date[2]);
            int currentYear = LocalDate.now().getYear();
            int age =  currentYear - year;
            if(age > 18 ){
                return true;
            }
        }
        return false;
    }

}
