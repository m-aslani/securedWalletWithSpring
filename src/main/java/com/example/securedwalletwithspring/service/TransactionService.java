package com.example.securedwalletwithspring.service;

import com.example.securedwalletwithspring.dto.TransactionDto;
import com.example.securedwalletwithspring.dto.TransactionHistoryDto;
import com.example.securedwalletwithspring.entity.Account;
import com.example.securedwalletwithspring.entity.Transaction;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.entity.Wallet;
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
    private WalletService walletService;

    @Autowired
    private UserService userService;

    public void addMoney(TransactionDto transactionDto) {
        checkOwnerOfAccount(transactionDto.getSenderNationalID() , transactionDto.getSender());
            if (transactionDto.getSender().equals(transactionDto.getReceiver())) {
                Optional<Account> account = accountRepository.findByAccountNumber(transactionDto.getSender());
                if (account.isPresent() && account.get().isActive()) {
                    double todayBalance = getTodayTotalTransactions(account.get());
                    if(todayBalance + transactionDto.getAmount() < 1000) { // consider ceiling is 1000$ per day
                        double newBalance = transactionDto.getAmount() + account.get().getAccountBalance();
                        account.get().setAccountBalance(newBalance);
                        accountRepository.save(account.get());
                        walletService.updateTotalWalletBalance(account.get().getWallet());
                        createTransaction(transactionDto, account.get());
                    }else {
                        throw new InvalidTransactionException("you reached your today's ceiling.");
                    }
                } else {
                    throw new AccountNotFoundException("Account number: " + transactionDto.getSender() + " does not exist or is disabled");
                }
            } else {
                throw new InvalidTransactionException("Sender and Receiver must be the same");
            }
    }

    public void transferMoney(TransactionDto transactionDto) {
        checkOwnerOfAccount(transactionDto.getSenderNationalID() , transactionDto.getSender());
        if(!transactionDto.getSender().equals(transactionDto.getReceiver())) {
            Optional<Account> senderAccount = accountRepository.findByAccountNumber(transactionDto.getSender());
            Optional<Account> receiverAccount = accountRepository.findByAccountNumber(transactionDto.getReceiver());
            if(senderAccount.isPresent() && receiverAccount.isPresent() && senderAccount.get().isActive() && receiverAccount.get().isActive()) {
                if(senderAccount.get().getAccountBalance() >= transactionDto.getAmount()) {
                    double todayBalance = getTodayTotalTransactions(senderAccount.get());
                    if(todayBalance + transactionDto.getAmount() < 1000) {
                        senderAccount.get().setAccountBalance(senderAccount.get().getAccountBalance() - transactionDto.getAmount());
                        receiverAccount.get().setAccountBalance(receiverAccount.get().getAccountBalance() + transactionDto.getAmount());
                        accountRepository.save(senderAccount.get());
                        accountRepository.save(receiverAccount.get());

                        walletService.updateTotalWalletBalance(senderAccount.get().getWallet());
                        walletService.updateTotalWalletBalance(receiverAccount.get().getWallet());

                        createTransaction(transactionDto , senderAccount.get());
                    }else {
                        throw new InvalidTransactionException("you reached your today's ceiling.");
                    }
                }else{
                    throw new InvalidTransactionException("Sender Balance is not enough");
                }
            }else{
                throw new AccountNotFoundException("Sender with account number: "+ transactionDto.getSender() + " OR Receiver with account number: "+ transactionDto.getReceiver() + " does not exist OR they are disabled");
            }
        }else {
            throw new InvalidTransactionException("Sender and Receiver cannot be the same");
        }
    }

    public List<Transaction> getTransactionHistory(TransactionHistoryDto transactionHistoryDto) {
        checkOwnerOfAccount(transactionHistoryDto.getSenderNationalID() , transactionHistoryDto.getAccountNumber());
        Optional<Account> account = accountRepository.findByAccountNumber(transactionHistoryDto.getAccountNumber());
        return transactionRepository.findByAccount(account.get());
    }

    public List<Transaction> getTransactionHistoryBetweenDate(TransactionHistoryDto transactionHistoryDto , LocalDate startDate , LocalDate endDate) {
        checkOwnerOfAccount(transactionHistoryDto.getSenderNationalID() , transactionHistoryDto.getAccountNumber());

            LocalDate now = LocalDate.now();
            if(startDate.isAfter(now)){
                throw new InvalidTransactionException("Invalid Start Date");
            }
            if(endDate.isAfter(now)){
                throw new InvalidTransactionException("Invalid End Date");
            }
            if(startDate.isAfter(endDate)){
                throw new InvalidTransactionException("Start Date cannot be after End Date");
            }
            return transactionRepository.findByTimestampBetween(startDate.atStartOfDay(), endDate.atStartOfDay());
    }

    //Helper Methods :

    public void createTransaction(TransactionDto transactionDto, Account account){
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setTransactionType(transactionDto.getTransactionType());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setAccount(account);
        transactionRepository.save(transaction);
    }

    public void checkOwnerOfAccount(String nationalId , String accountNumber) {
        Optional<User> user = userService.getUserByNationalId(nationalId);
            if(user.get().getWallet() != null) {
                List<Account> accounts = accountRepository.findByWallet(user.get().getWallet());
                Account foundAccount = accounts.stream().filter(account->account.getAccountNumber().equals(accountNumber)).findFirst().orElse(null);
                if(foundAccount == null) {
                    throw new InvalidTransactionException("Sender National Id "+nationalId+" does not have access to account number : "+accountNumber + ", or account not found");
                }
            }else {
                throw new AccountNotFoundException("some error occurred.");
            }
    }


    public double getTodayTotalTransactions(Account account) {
        LocalDate today = LocalDate.now();
        List<Transaction> transactions = transactionRepository.findByAccount(account)
                .stream().filter(t->t.getTimestamp().toLocalDate().equals(today))
                .toList();
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }
}


