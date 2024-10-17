package com.example.securedwalletwithspring.serviceTests;

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
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUserSuccess(){
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setNationalId("1234567891");
        userRegistrationDto.setFirstname("Masoumeh");
        userRegistrationDto.setLastname("Aslani");
        userRegistrationDto.setEmail("masoumeh@gmail.com");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setPhoneNumber("09127270451");
        userRegistrationDto.setBirthDate("1/14/2001");
        userRegistrationDto.setGender("female");
        userRegistrationDto.setMilitaryStatus(false);
        userRegistrationDto.setInitialAmount(50);

        when(passwordEncoder.encode(anyString())).thenReturn("encryptedPassword");

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());
        when((accountRepository.findByAccountIban(anyString()))).thenReturn(Optional.empty());

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = userService.registerUser(userRegistrationDto);

        assertNotNull(registeredUser);
        assertEquals(registeredUser.getNationalId(), userRegistrationDto.getNationalId());
        assertEquals(registeredUser.getFirstname(), userRegistrationDto.getFirstname());
        assertEquals(registeredUser.getLastname(), userRegistrationDto.getLastname());
        assertEquals(registeredUser.getEmail(), userRegistrationDto.getEmail());
        assertEquals(registeredUser.getPassword(), "encryptedPassword");
        assertEquals(registeredUser.getPhoneNumber(), userRegistrationDto.getPhoneNumber());
        assertEquals(registeredUser.getBirthDate(), userRegistrationDto.getBirthDate());
        assertEquals(registeredUser.getGender(), userRegistrationDto.getGender());

        assertNotNull(registeredUser.getAccount());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testLoginUserSuccess(){
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setNationalId("1234567891");
        userLoginDto.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");

        String token = userService.loginUser(userLoginDto);

        assertNotNull(token);
        assertEquals(token, "jwtToken");
        verify(authenticationManager , times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(any(UserDetails.class));
    }

    @Test
    public void testRegisterUser_militaryStatusFailure(){
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setNationalId("123456788");
        userRegistrationDto.setFirstname("Ali");
        userRegistrationDto.setLastname("Amiri");
        userRegistrationDto.setEmail("Ali@gmail.com");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setPhoneNumber("09127270451");
        userRegistrationDto.setBirthDate("1/14/2001");
        userRegistrationDto.setGender("male");
        userRegistrationDto.setMilitaryStatus(false); // false for who have not passed their military service, true for who have passed it.
        userRegistrationDto.setInitialAmount(50);

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.registerUser(userRegistrationDto));

        assertEquals("you can not create account due to your military status" , exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegisterUser_initialAmountFailure(){
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setNationalId("123456788");
        userRegistrationDto.setFirstname("Ali");
        userRegistrationDto.setLastname("Amiri");
        userRegistrationDto.setEmail("Ali@gmail.com");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setPhoneNumber("09127270451");
        userRegistrationDto.setBirthDate("1/14/2001");
        userRegistrationDto.setGender("male");
        userRegistrationDto.setMilitaryStatus(true); // false for who have not passed their military service, true for who have passed it.
        userRegistrationDto.setInitialAmount(5);

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.registerUser(userRegistrationDto));
        assertEquals("Initial amount must be greater than 10$" , exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

}
