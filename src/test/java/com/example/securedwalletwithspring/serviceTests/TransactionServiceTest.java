package com.example.securedwalletwithspring.serviceTests;

import com.example.securedwalletwithspring.dto.TransactionDto;
import com.example.securedwalletwithspring.dto.TransactionHistoryDto;
import com.example.securedwalletwithspring.entity.Account;
import com.example.securedwalletwithspring.entity.Transaction;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.entity.Wallet;
import com.example.securedwalletwithspring.exception.InvalidTransactionException;
import com.example.securedwalletwithspring.repository.AccountRepository;
import com.example.securedwalletwithspring.repository.TransactionRepository;
import com.example.securedwalletwithspring.repository.UserRepository;
import com.example.securedwalletwithspring.service.TransactionService;
import com.example.securedwalletwithspring.service.UserService;
import com.example.securedwalletwithspring.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testAddMoneySuccess() {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setSenderNationalID("1234567890");
        transactionDto.setSender("1000000000000000");
        transactionDto.setReceiver("1000000000000000");
        transactionDto.setAmount(500);
        transactionDto.setTransactionType("credit");

        Account account = new Account();
        account.setAccountNumber("1000000000000000");
        account.setAccountBalance(100);
        account.setActive(true);

        List<Account> accounts = List.of(account);

        User user = new User();
        user.setNationalId("1234567890");

        Wallet wallet = new Wallet();
        user.setWallet(wallet);

        account.setWallet(wallet);

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        when(userService.getUserByNationalId(user.getNationalId())).thenReturn(Optional.of(user));
        when(transactionRepository.findByAccount(any(Account.class))).thenReturn(Arrays.asList());
        when(accountRepository.findByWallet(user.getWallet())).thenReturn(accounts);

        transactionService.addMoney(transactionDto);

        assertEquals(account.getAccountBalance(), 600);
        verify(accountRepository).save(account);
        verify(walletService).updateTotalWalletBalance(account.getWallet());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    public void testTransferMoneySuccess() {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setSenderNationalID("1234567890");
        transactionDto.setSender("1000000000000000");
        transactionDto.setReceiver("1000000000000001");
        transactionDto.setAmount(500);
        transactionDto.setTransactionType("transfer");

        Account senderAccount = new Account();
        senderAccount.setAccountNumber("1000000000000000");
        senderAccount.setAccountBalance(600);
        senderAccount.setActive(true);

        Account receiverAccount = new Account();
        receiverAccount.setAccountNumber("1000000000000001");
        receiverAccount.setAccountBalance(100);
        receiverAccount.setActive(true);

        List<Account> accounts = List.of(senderAccount);

        User user = new User();
        user.setNationalId("1234567890");

        Wallet wallet = new Wallet();
        user.setWallet(wallet);

        senderAccount.setWallet(wallet);

        when(accountRepository.findByAccountNumber("1000000000000000")).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("1000000000000001")).thenReturn(Optional.of(receiverAccount));
        when(userService.getUserByNationalId(user.getNationalId())).thenReturn(Optional.of(user));
        when(transactionRepository.findByAccount(any(Account.class))).thenReturn(Arrays.asList());
        when(accountRepository.findByWallet(user.getWallet())).thenReturn(accounts);

        transactionService.transferMoney(transactionDto);

        assertEquals(senderAccount.getAccountBalance(), 100);
        assertEquals(receiverAccount.getAccountBalance(), 600);

        verify(accountRepository, times(2)).save(any(Account.class));
        verify(walletService, times(2)).updateTotalWalletBalance(any());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    public void testGetTransactionHistorySuccess() {
        TransactionHistoryDto transactionHistoryDto = new TransactionHistoryDto();
        transactionHistoryDto.setSenderNationalID("1234567890");
        transactionHistoryDto.setAccountNumber("1000000000000000");

        Account account = new Account();
        account.setAccountNumber("1000000000000000");

        List<Account> accounts = List.of(account);

        User user = new User();
        user.setNationalId("1234567890");

        Wallet wallet = new Wallet();
        user.setWallet(wallet);

        account.setWallet(wallet);

        List<Transaction> transactions = Arrays.asList(
                new Transaction(1L, 100, "credit", LocalDateTime.now(), account),
                new Transaction(2L, 200, "debit", LocalDateTime.now(), account)
        );

        when(userService.getUserByNationalId(user.getNationalId())).thenReturn(Optional.of(user));
        when(accountRepository.findByWallet(user.getWallet())).thenReturn(accounts);

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccount(account)).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionHistory(transactionHistoryDto);

        assertEquals(2, result.size());
        assertEquals(100, result.get(0).getAmount());
        verify(transactionRepository).findByAccount(account);
    }

    @Test
    public void testGetTransactionHistoryBetweenDateSuccess() {
        TransactionHistoryDto transactionHistoryDto = new TransactionHistoryDto();
        transactionHistoryDto.setSenderNationalID("1234567890");
        transactionHistoryDto.setAccountNumber("1000000000000000");

        Account account = new Account();
        account.setAccountNumber("1000000000000000");

        List<Account> accounts = List.of(account);

        User user = new User();
        user.setNationalId("1234567890");

        Wallet wallet = new Wallet();
        user.setWallet(wallet);

        account.setWallet(wallet);

        when(userService.getUserByNationalId(user.getNationalId())).thenReturn(Optional.of(user));
        when(accountRepository.findByWallet(user.getWallet())).thenReturn(accounts);

        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 10);

        List<Transaction> transactions = Arrays.asList(
                new Transaction(1L, 600, "credit", LocalDateTime.of(2024, 2, 2, 3, 50), account),
                new Transaction(2L, 300, "credit", LocalDateTime.of(2024, 4, 12, 5, 20), account)
        );

        when(transactionRepository.findByTimestampBetween(startDate.atStartOfDay(), endDate.atStartOfDay()))
                .thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionHistoryBetweenDate(transactionHistoryDto, startDate, endDate);

        assertEquals(2, result.size()); // Since 2 transactions fall between the date range
        verify(transactionRepository).findByTimestampBetween(startDate.atStartOfDay(), endDate.atStartOfDay());
    }

    @Test
    public void testGetTransactionsBetween_invalidStartDate() {
        TransactionHistoryDto transactionHistoryDto = new TransactionHistoryDto();
        transactionHistoryDto.setSenderNationalID("1234567890");
        transactionHistoryDto.setAccountNumber("1000000000000000");

        Account account = new Account();
        account.setAccountNumber("1000000000000000");

        List<Account> accounts = List.of(account);

        User user = new User();
        user.setNationalId("1234567890");

        Wallet wallet = new Wallet();
        user.setWallet(wallet);

        account.setWallet(wallet);

        when(userService.getUserByNationalId(user.getNationalId())).thenReturn(Optional.of(user));
        when(accountRepository.findByWallet(user.getWallet())).thenReturn(accounts);

        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 10);

        List<Transaction> transactions = Arrays.asList(
                new Transaction(1L, 600, "credit", LocalDateTime.of(2024, 2, 2, 3, 50), account),
                new Transaction(2L, 300, "credit", LocalDateTime.of(2024, 4, 12, 5, 20), account)
        );

//        when(transactionRepository.findByTimestampBetween(startDate.atStartOfDay(), endDate.atStartOfDay()))
//                .thenReturn(transactions);

        Exception exception = assertThrows(InvalidTransactionException.class, ()-> transactionService.getTransactionHistoryBetweenDate(transactionHistoryDto,startDate,endDate));

        verify(transactionRepository , never()).findByTimestampBetween(any(), any());
        assertEquals(exception.getMessage() , "Invalid Start Date");
    }

    @Test
    public void testGetTransactionsBetween_invalidEndDate() {
        TransactionHistoryDto transactionHistoryDto = new TransactionHistoryDto();
        transactionHistoryDto.setSenderNationalID("1234567890");
        transactionHistoryDto.setAccountNumber("1000000000000000");

        Account account = new Account();
        account.setAccountNumber("1000000000000000");

        List<Account> accounts = List.of(account);

        User user = new User();
        user.setNationalId("1234567890");

        Wallet wallet = new Wallet();
        user.setWallet(wallet);

        account.setWallet(wallet);

        when(userService.getUserByNationalId(user.getNationalId())).thenReturn(Optional.of(user));
        when(accountRepository.findByWallet(user.getWallet())).thenReturn(accounts);

        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 10, 10);

        List<Transaction> transactions = Arrays.asList(
                new Transaction(1L, 600, "credit", LocalDateTime.of(2024, 2, 2, 3, 50), account),
                new Transaction(2L, 300, "credit", LocalDateTime.of(2024, 4, 12, 5, 20), account)
        );

//        when(transactionRepository.findByTimestampBetween(startDate.atStartOfDay(), endDate.atStartOfDay()))
//                .thenReturn(transactions);

        Exception exception = assertThrows(InvalidTransactionException.class, ()-> transactionService.getTransactionHistoryBetweenDate(transactionHistoryDto,startDate,endDate));

        verify(transactionRepository , never()).findByTimestampBetween(any(), any());
        assertEquals(exception.getMessage() , "Invalid End Date");
    }

    @Test
    public void testGetTransactionsBetween_invalidStartEndDate() {
        TransactionHistoryDto transactionHistoryDto = new TransactionHistoryDto();
        transactionHistoryDto.setSenderNationalID("1234567890");
        transactionHistoryDto.setAccountNumber("1000000000000000");

        Account account = new Account();
        account.setAccountNumber("1000000000000000");

        List<Account> accounts = List.of(account);

        User user = new User();
        user.setNationalId("1234567890");

        Wallet wallet = new Wallet();
        user.setWallet(wallet);

        account.setWallet(wallet);

        when(userService.getUserByNationalId(user.getNationalId())).thenReturn(Optional.of(user));
        when(accountRepository.findByWallet(user.getWallet())).thenReturn(accounts);

        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 10, 10);

        List<Transaction> transactions = Arrays.asList(
                new Transaction(1L, 600, "credit", LocalDateTime.of(2024, 2, 2, 3, 50), account),
                new Transaction(2L, 300, "credit", LocalDateTime.of(2024, 4, 12, 5, 20), account)
        );

//        when(transactionRepository.findByTimestampBetween(startDate.atStartOfDay(), endDate.atStartOfDay()))
//                .thenReturn(transactions);

        Exception exception = assertThrows(InvalidTransactionException.class, ()-> transactionService.getTransactionHistoryBetweenDate(transactionHistoryDto,startDate,endDate));

        verify(transactionRepository , never()).findByTimestampBetween(any(), any());
        assertEquals(exception.getMessage() , "Start Date cannot be after End Date");
    }

    @Test
    public void testGetTodayTotalTransactionsSuccess() {
        Account account = new Account();
        account.setAccountNumber("1000000000000000");

        List<Transaction> transactions = Arrays.asList(
                new Transaction(1L, 100, "credit", LocalDateTime.now(), account),
                new Transaction(2L, 200, "debit", LocalDateTime.now(), account)
        );

        when(transactionRepository.findByAccount(account)).thenReturn(transactions);

        double total = transactionService.getTodayTotalTransactions(account);

        assertEquals(300, total);
        verify(transactionRepository).findByAccount(account);
    }
}
