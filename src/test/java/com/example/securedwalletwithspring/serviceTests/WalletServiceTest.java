package com.example.securedwalletwithspring.serviceTests;

import com.example.securedwalletwithspring.dto.UserLoginDto;
import com.example.securedwalletwithspring.dto.UserRegistrationDto;
import com.example.securedwalletwithspring.entity.Account;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.entity.Wallet;
import com.example.securedwalletwithspring.entity.enums.Gender;
import com.example.securedwalletwithspring.exception.UserNotFoundException;
import com.example.securedwalletwithspring.repository.AccountRepository;
import com.example.securedwalletwithspring.repository.UserRepository;
import com.example.securedwalletwithspring.repository.WalletRepository;
import com.example.securedwalletwithspring.service.*;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;


//@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class WalletServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private WalletRepository walletRepository;

    @MockBean
    private PasswordEncoder passwordEncoder; // Use @MockBean for Spring integration

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private AccountService accountService;

    @InjectMocks
    private WalletService walletService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testRegisterUserSuccess() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setNationalId("1234567891");
        userRegistrationDto.setFirstname("Masoumeh");
        userRegistrationDto.setLastname("Aslani");
        userRegistrationDto.setEmail("masoumeh@gmail.com");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setPhoneNumber("09127270451");
        userRegistrationDto.setBirthDate("1/14/2001");
        userRegistrationDto.setGender(Gender.FEMALE);
        userRegistrationDto.setMilitaryStatus(false);
        userRegistrationDto.setInitialAmount(50);

        User user = new User();
        user.setNationalId(userRegistrationDto.getNationalId());

        Wallet wallet = new Wallet();
        Account account = new Account();
        account.setAccountBalance(100);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(accountService.createAccount(anyDouble())).thenReturn(account);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = walletService.registerUser(userRegistrationDto);

        verify(userRepository).save(any(User.class));
        verify(walletRepository).save(any(Wallet.class));
        verify(accountRepository).save(any(Account.class));

        assertNotNull(registeredUser);
        assertEquals(registeredUser.getNationalId(), userRegistrationDto.getNationalId());
        assertEquals(registeredUser.getFirstname(), userRegistrationDto.getFirstname());
        assertEquals(registeredUser.getLastname(), userRegistrationDto.getLastname());
        assertEquals(registeredUser.getEmail(), userRegistrationDto.getEmail());
        assertEquals(registeredUser.getPassword(), "encodedPassword");
        assertEquals(registeredUser.getPhoneNumber(), userRegistrationDto.getPhoneNumber());
        assertEquals(registeredUser.getBirthDate(), userRegistrationDto.getBirthDate());
        assertEquals(registeredUser.getGender(), userRegistrationDto.getGender());

        assertNotNull(registeredUser.getWallet());
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    public void testLoginUserSuccess() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setNationalId("1234567891");
        loginDto.setPassword("password123");

        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(customUserDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");

        String token = walletService.loginUser(loginDto);

        assertEquals("jwtToken", token);
    }
    @Test
    public void testRegisterUser_militaryStatusFailure() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setNationalId("1234567891");
        userRegistrationDto.setFirstname("Ali");
        userRegistrationDto.setLastname("Aslani");
        userRegistrationDto.setEmail("ali@gmail.com");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setPhoneNumber("09127270451");
        userRegistrationDto.setBirthDate("1/14/2001");
        userRegistrationDto.setGender(Gender.MALE);
        userRegistrationDto.setMilitaryStatus(false);
        userRegistrationDto.setInitialAmount(50);

        User user = new User();
        user.setNationalId(userRegistrationDto.getNationalId());

        Wallet wallet = new Wallet();
        Account account = new Account();
        account.setAccountBalance(100);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(accountService.createAccount(anyDouble())).thenReturn(account);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Exception exception = assertThrows(UserNotFoundException.class, () -> walletService.registerUser(userRegistrationDto));

        assertEquals("you can not create account due to your military status" , exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateTotalWalletBalance() {
        Wallet wallet = new Wallet();
        Account account1 = new Account();
        account1.setAccountBalance(100.0);
        Account account2 = new Account();
        account2.setAccountBalance(50.0);

        List<Account> accounts = Arrays.asList(account1, account2);

        when(accountRepository.findByWallet(wallet)).thenReturn(accounts);

        walletService.updateTotalWalletBalance(wallet);

        assertEquals(150.0, wallet.getTotalBalance(), 0.01);
        verify(walletRepository).save(wallet);
    }

}
