package com.example.securedwalletwithspring.service;

import com.example.securedwalletwithspring.dto.TransactionDto;
import com.example.securedwalletwithspring.dto.TransactionHistoryDto;
import com.example.securedwalletwithspring.entity.Account;
import com.example.securedwalletwithspring.entity.Transaction;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.exception.AccountNotFoundException;
import com.example.securedwalletwithspring.exception.InvalidTransactionException;
import com.example.securedwalletwithspring.repository.AccountRepository;
import com.example.securedwalletwithspring.repository.TransactionRepository;
import com.example.securedwalletwithspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    public void addMoney(TransactionDto transactionDto) {
        Account existingAccount = checkOwnerOfAccount(transactionDto);
        if(existingAccount.getAccountNumber().equals(transactionDto.getSender())) {
            if (transactionDto.getSender().equals(transactionDto.getReceiver())) {
                Optional<Account> account = accountRepository.findByAccountNumber(transactionDto.getSender());
                if (account.isPresent()) {
                    double todayBalance = getTodayTotalTransactions(account.get());
                    if(todayBalance + transactionDto.getAmount() < 1000) { // consider ceiling is 1000$ per day
                        double newBalance = transactionDto.getAmount() + account.get().getAccountBalance();
                        account.get().setAccountBalance(newBalance);
                        accountRepository.save(account.get());

                        createTransaction(transactionDto, account.get());
                    }else {
                        throw new InvalidTransactionException("you reached your today's ceiling.");
                    }
                } else {
                    throw new AccountNotFoundException("Account number: " + transactionDto.getSender() + " does not exist");
                }
            } else {
                throw new InvalidTransactionException("Sender and Receiver cannot be the same");
            }
        }else{
            throw new InvalidTransactionException("Sender National Id "+transactionDto.getSenderNationalID()+" does not have access to account number : "+transactionDto.getSender());
        }
    }

    public void transferMoney(TransactionDto transactionDto) {
        if(!transactionDto.getSender().equals(transactionDto.getReceiver())) {
            Optional<Account> senderAccount = accountRepository.findByAccountNumber(transactionDto.getSender());
            Optional<Account> receiverAccount = accountRepository.findByAccountNumber(transactionDto.getReceiver());
            if(senderAccount.isPresent() && receiverAccount.isPresent()) {
                if(senderAccount.get().getAccountBalance() >= transactionDto.getAmount()) {
                    double todayBalance = getTodayTotalTransactions(senderAccount.get());
                    if(todayBalance + transactionDto.getAmount() < 1000) {
                        senderAccount.get().setAccountBalance(senderAccount.get().getAccountBalance() - transactionDto.getAmount());
                        receiverAccount.get().setAccountBalance(receiverAccount.get().getAccountBalance() + transactionDto.getAmount());
                        accountRepository.save(senderAccount.get());
                        accountRepository.save(receiverAccount.get());

                        createTransaction(transactionDto , senderAccount.get());
                    }else {
                        throw new InvalidTransactionException("you reached your today's ceiling.");
                    }
                }else{
                    throw new InvalidTransactionException("Sender Balance is not enough");
                }
            }else{
                throw new AccountNotFoundException("Sender with account number: "+ transactionDto.getSender() + " and Receiver with account number: "+ transactionDto.getReceiver() + " does not exist");
            }
        }else {
            throw new InvalidTransactionException("Sender and Receiver cannot be the same");
        }
    }

    public List<Transaction> getTransactionHistory(TransactionHistoryDto transactionHistoryDto) {
        Optional<Account> account = accountRepository.findByAccountNumber(transactionHistoryDto.getAccountNumber());
        return transactionRepository.findByAccount(account.get());
    }

    public void createTransaction(TransactionDto transactionDto, Account account){
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setTransactionType(transactionDto.getTransactionType());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setAccount(account);
        transactionRepository.save(transaction);
    }

    public Account checkOwnerOfAccount(TransactionDto transactionDto) {
        Optional<User> user = userRepository.findByNationalId(transactionDto.getSenderNationalID());
        if(user.isPresent()) {
            if(user.get().getAccount() != null) {
//                System.out.println(account.get().getAccountBalance());
                return user.get().getAccount();
            }
        }
//        return user.get().getAccount();
        throw new AccountNotFoundException("Account number: "+transactionDto.getSenderNationalID() + " does not exist");
    }

    public double getTodayTotalTransactions(Account account) {
        LocalDate today = LocalDate.now();
        List<Transaction> transactions = transactionRepository.findByAccount(account)
                .stream().filter(t->t.getTimestamp().toLocalDate().equals(today))
                .toList();
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }
}


