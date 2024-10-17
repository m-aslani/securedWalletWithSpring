package com.example.securedwalletwithspring.serviceTests;

import com.example.securedwalletwithspring.dto.TransactionDto;
import com.example.securedwalletwithspring.dto.TransactionHistoryDto;
import com.example.securedwalletwithspring.entity.Account;
import com.example.securedwalletwithspring.entity.Transaction;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.exception.InvalidTransactionException;
import com.example.securedwalletwithspring.repository.AccountRepository;
import com.example.securedwalletwithspring.repository.TransactionRepository;
import com.example.securedwalletwithspring.repository.UserRepository;
import com.example.securedwalletwithspring.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddMoneySuccess(){
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(10);
        transactionDto.setTransactionType("credit");
        transactionDto.setSenderNationalID("123456789");
        transactionDto.setSender("1000000000000000");
        transactionDto.setReceiver("1000000000000000");

        Account account = new Account();
        account.setAccountNumber("1000000000000000");
        account.setAccountBalance(100);

        User user = new User();
        user.setNationalId("123456789");
        user.setAccount(account);

        when(accountRepository.findByAccountNumber("1000000000000000")).thenReturn(Optional.of(account));
        when(userRepository.findByNationalId("123456789")).thenReturn(Optional.of(user));

        when(transactionRepository.findByAccount(account)).thenReturn(new ArrayList<>());


        transactionService.addMoney(transactionDto);

        assertEquals(account.getAccountBalance(), 110);
        verify(transactionRepository,times(1)).save(any(Transaction.class));
    }

    @Test
    public void testAddMoney_DailyCeilingFailure(){
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(900);
        transactionDto.setTransactionType("credit");
        transactionDto.setSenderNationalID("123456789");
        transactionDto.setSender("1000000000000000");
        transactionDto.setReceiver("1000000000000000");

        Account account = new Account();
        account.setAccountNumber("1000000000000000");
        account.setAccountBalance(110);

        User user = new User();
        user.setNationalId("123456789");
        user.setAccount(account);

        when(accountRepository.findByAccountNumber("1000000000000000")).thenReturn(Optional.of(account));
        when(userRepository.findByNationalId("123456789")).thenReturn(Optional.of(user));

        List<Transaction> transactions = List.of(new Transaction(1L,600,"credit", LocalDateTime.now(),account),
                new Transaction(2L,300,"credit", LocalDateTime.now(),account));

        when(transactionRepository.findByAccount(account)).thenReturn(transactions);

        Exception exception = assertThrows(InvalidTransactionException.class , ()-> transactionService.addMoney(transactionDto));
        assertEquals(exception.getMessage(),"you reached your today's ceiling.");
    }

    @Test
    public void testAddMoney_InvalidSenderFailure(){
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(900);
        transactionDto.setTransactionType("credit");
        transactionDto.setSenderNationalID("123456789");
        transactionDto.setSender("1000000000000000");
        transactionDto.setReceiver("1000000000000000");

        Account account = new Account();
        account.setAccountNumber("1000000000000001");
        account.setAccountBalance(110);

        User user = new User();
        user.setNationalId("123456789");
        user.setAccount(account);

        when(accountRepository.findByAccountNumber("1000000000000000")).thenReturn(Optional.of(account));
        when(userRepository.findByNationalId("123456789")).thenReturn(Optional.of(user));


        when(transactionRepository.findByAccount(account)).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(InvalidTransactionException.class , ()-> transactionService.addMoney(transactionDto));
        assertEquals(exception.getMessage(),"Sender National Id "+transactionDto.getSenderNationalID()+ " does not have access to account number : "+transactionDto.getSender());
    }

    @Test
    public void testAddMoney_InvalidSenderReceiver(){
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(900);
        transactionDto.setTransactionType("credit");
        transactionDto.setSenderNationalID("123456789");
        transactionDto.setSender("1000000000000000");
        transactionDto.setReceiver("1000000000000001");

        Account account = new Account();
        account.setAccountNumber("1000000000000000");
        account.setAccountBalance(110);

        User user = new User();
        user.setNationalId("123456789");
        user.setAccount(account);

        when(accountRepository.findByAccountNumber("1000000000000000")).thenReturn(Optional.of(account));
        when(userRepository.findByNationalId("123456789")).thenReturn(Optional.of(user));


        when(transactionRepository.findByAccount(account)).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(InvalidTransactionException.class , ()-> transactionService.addMoney(transactionDto));
        assertEquals(exception.getMessage(),"Sender and Receiver must be the same");
    }

    @Test
    public void testTransferMoneySuccess(){
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(100);
        transactionDto.setTransactionType("debit");
        transactionDto.setSenderNationalID("123456789");
        transactionDto.setSender("1000000000000000");
        transactionDto.setReceiver("1000000000000001");

        Account account = new Account();
        account.setAccountNumber("1000000000000000");
        account.setAccountBalance(500);

        User user = new User();
        user.setNationalId("123456789");
        user.setAccount(account);

        Account account2 = new Account();
        account2.setAccountNumber("1000000000000001");
        account2.setAccountBalance(100);

        when(accountRepository.findByAccountNumber("1000000000000000")).thenReturn(Optional.of(account));
        when(accountRepository.findByAccountNumber("1000000000000001")).thenReturn(Optional.of(account2));
        when(userRepository.findByNationalId("123456789")).thenReturn(Optional.of(user));

        when(transactionRepository.findByAccount(account)).thenReturn(new ArrayList<>());


        transactionService.transferMoney(transactionDto);

        assertEquals(account.getAccountBalance(), 400);
        assertEquals(account2.getAccountBalance(), 200);
        verify(transactionRepository,times(1)).save(any(Transaction.class));
    }

    @Test
    public void testTransferMoney_BalanceNotEnough(){
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(1000);
        transactionDto.setTransactionType("debit");
        transactionDto.setSenderNationalID("123456789");
        transactionDto.setSender("1000000000000000");
        transactionDto.setReceiver("1000000000000001");

        Account account = new Account();
        account.setAccountNumber("1000000000000000");
        account.setAccountBalance(500);

        User user = new User();
        user.setNationalId("123456789");
        user.setAccount(account);

        Account account2 = new Account();
        account2.setAccountNumber("1000000000000001");
        account2.setAccountBalance(100);

        when(accountRepository.findByAccountNumber("1000000000000000")).thenReturn(Optional.of(account));
        when(accountRepository.findByAccountNumber("1000000000000001")).thenReturn(Optional.of(account2));
        when(userRepository.findByNationalId("123456789")).thenReturn(Optional.of(user));

        when(transactionRepository.findByAccount(account)).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(InvalidTransactionException.class , ()-> transactionService.transferMoney(transactionDto));
        assertEquals("Sender Balance is not enough", exception.getMessage());
    }

    @Test
    public void testGetTransactionsHistorySuccess(){
        TransactionHistoryDto transactionHistoryDto = new TransactionHistoryDto();
        transactionHistoryDto.setAccountNumber("1000000000000000");

        Account account = new Account();
        account.setAccountNumber("1000000000000000");

        List<Transaction> transactions = List.of(new Transaction(1L,600,"credit", LocalDateTime.now(),account),
                new Transaction(2L,300,"credit", LocalDateTime.now(),account));

        when(accountRepository.findByAccountNumber("1000000000000000")).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccount(account)).thenReturn(transactions);

        List<Transaction> history = transactionService.getTransactionHistory(transactionHistoryDto);

        assertEquals(history.size(), 2);
        assertEquals(history.get(0).getAmount(), 600);
        assertEquals(history.get(1).getAmount(), 300);
    }
}
