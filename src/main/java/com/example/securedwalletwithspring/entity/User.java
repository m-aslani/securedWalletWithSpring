package com.example.securedwalletwithspring.entity;

import com.example.securedwalletwithspring.entity.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_TB")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull(message = "user militaryStatus can NOT be Empty!")
    private boolean militaryStatus;

//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL , orphanRemoval = true)
//    private Account account;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL , orphanRemoval = true)
    private Wallet wallet;

    public boolean checkMilitaryStatus(String birthDate , Gender gender , boolean militaryStatus) {
        if(gender == Gender.MALE && !militaryStatus) {
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
