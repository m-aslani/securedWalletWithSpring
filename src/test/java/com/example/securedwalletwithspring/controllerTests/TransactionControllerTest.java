package com.example.securedwalletwithspring.controllerTests;

import static org.mockito.Mockito.*;

import com.example.securedwalletwithspring.controller.TransactionController;
import com.example.securedwalletwithspring.dto.TransactionDto;
import com.example.securedwalletwithspring.dto.TransactionHistoryDto;
import com.example.securedwalletwithspring.entity.Transaction;
import com.example.securedwalletwithspring.service.CustomUserDetailsService;
import com.example.securedwalletwithspring.service.JwtService;
import com.example.securedwalletwithspring.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private TransactionController transactionController;

    private String token;

    @BeforeEach
    public void setUp() throws Exception {
        token = "Bearer " + generateMockToken();
    }

    private String generateMockToken() {
        return "jwtToken";
    }

    @Test
    public void testAddMoneySuccess() throws Exception {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(100);
        transactionDto.setTransactionType("credit");
        transactionDto.setSenderNationalID("1234567891");
        transactionDto.setSender("1000000000000000");
        transactionDto.setReceiver("1000000000000000");

        mockMvc.perform(post("/transaction/add-money")
                .header("Authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDto))
        ).andExpect(status().isOk())
                .andExpect(content().string("Transaction added"));
    }

    @Test
    public void testTransferMoneySuccess() throws Exception {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(100);
        transactionDto.setTransactionType("credit");
        transactionDto.setSenderNationalID("1234567891");
        transactionDto.setSender("1000000000000000");
        transactionDto.setReceiver("1000000000000001");

        mockMvc.perform(post("/transaction/transfer-money")
                        .header("Authorization",token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto))
                ).andExpect(status().isOk())
                .andExpect(content().string("Transaction transferred"));
    }

    @Test
    public void testGetTransactionsSuccess() throws Exception {
        TransactionHistoryDto transactionDto = new TransactionHistoryDto();
        transactionDto.setAccountNumber("1000000000000000");

        when(transactionService.getTransactionHistory(any(TransactionHistoryDto.class))).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/transaction")
                        .header("Authorization",token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testFieldValidation() throws Exception {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(0);
        transactionDto.setTransactionType("");
        transactionDto.setSenderNationalID("");
        transactionDto.setSender("");
        transactionDto.setReceiver("");

        mockMvc.perform(post("/transaction/add-money")
                        .header("Authorization",token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.senderNationalID").value("Sender National ID can not be Empty!"))
                .andExpect(jsonPath("$.amount").value("Transaction amount must be greater than 1$!"))
                .andExpect(jsonPath("$.transactionType").value("Transaction type can not be empty!"))
                .andExpect(jsonPath("$.sender").value("sender account number can not be empty!"))
                .andExpect(jsonPath("$.receiver").value("receiver account number can not be empty!"));
    }

}
