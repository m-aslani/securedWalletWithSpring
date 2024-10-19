package com.example.securedwalletwithspring.serviceTests;

import com.example.securedwalletwithspring.dto.EditedUserDto;
import com.example.securedwalletwithspring.dto.UserLoginDto;
import com.example.securedwalletwithspring.dto.UserRegistrationDto;
import com.example.securedwalletwithspring.entity.Account;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.exception.UserNotFoundException;
import com.example.securedwalletwithspring.repository.AccountRepository;
import com.example.securedwalletwithspring.repository.UserRepository;
import com.example.securedwalletwithspring.service.JwtService;
import com.example.securedwalletwithspring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserByNationalIdSuccess() {
        String nationalId = "1234567891";
        User user = new User();
        user.setNationalId(nationalId);

        when(userRepository.findByNationalId(nationalId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByNationalId(nationalId);

        assertEquals(user, result.get());
        verify(userRepository).findByNationalId(nationalId);
    }


    @Test
    public void testUpdateUserPhoneNumberSuccess() {
        EditedUserDto editedUserDto = new EditedUserDto();
        editedUserDto.setPhoneNumber("09123456789");

        User user = new User();
        user.setPhoneNumber("0987654321");

        when(userRepository.save(any(User.class))).thenReturn(user);

        String updatedPhone = userService.updateUserPhoneNumber(editedUserDto, user);

        assertEquals("09123456789", updatedPhone);
        verify(userRepository).save(user);
    }

    @Test
    public void testUpdateUserEmailSuccess() {
        EditedUserDto editedUserDto = new EditedUserDto();
        editedUserDto.setEmail("newemail@example.com");

        User user = new User();
        user.setEmail("oldemail@example.com");

        when(userRepository.save(any(User.class))).thenReturn(user);

        String updatedEmail = userService.updateUserEmail(editedUserDto, user);

        assertEquals("newemail@example.com", updatedEmail);
        verify(userRepository).save(user);
    }

    @Test
    public void testUpdateUserPasswordSuccess() {
        EditedUserDto editedUserDto = new EditedUserDto();
        editedUserDto.setPassword("newpassword");

        User user = new User();
        user.setPassword("oldpassword");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        String updatedPassword = userService.updateUserPassword(editedUserDto, user);

        assertEquals("newpassword", updatedPassword);
        verify(passwordEncoder).encode("newpassword");
        verify(userRepository).save(user);
    }

}
