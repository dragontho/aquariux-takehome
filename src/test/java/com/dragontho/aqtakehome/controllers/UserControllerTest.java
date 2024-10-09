package com.dragontho.aqtakehome.controllers;

import com.dragontho.aqtakehome.data.internapi.TransactionDto;
import com.dragontho.aqtakehome.data.internapi.TransactionDtoPage;
import com.dragontho.aqtakehome.data.internapi.WalletDtoPage;
import com.dragontho.aqtakehome.enums.TransactionType;
import com.dragontho.aqtakehome.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionDto mockTransactionDto;
    private WalletDtoPage mockWalletDtoPage;
    private TransactionDtoPage mockTransactionDtoPage;

    @BeforeEach
    void setUp() {
        mockTransactionDto = new TransactionDto();
        mockTransactionDto.setUsername("testUser");
        mockTransactionDto.setSymbol("BTCUSDT");
        mockTransactionDto.setAmount(new BigDecimal("0.5"));
        mockTransactionDto.setType(TransactionType.BUY);

        mockWalletDtoPage = new WalletDtoPage();
        mockWalletDtoPage.setData(Collections.emptyList());
        mockWalletDtoPage.setPages(1);
        mockWalletDtoPage.setTotal(0);

        mockTransactionDtoPage = new TransactionDtoPage();
        mockTransactionDtoPage.setData(Collections.emptyList());
        mockTransactionDtoPage.setPages(1);
        mockTransactionDtoPage.setTotal(0);
    }

    @Test
    void performTrade_shouldReturnTransactionDto() throws Exception {
        when(userService.executeTrade(any(TransactionDto.class))).thenReturn(mockTransactionDto);

        mockMvc.perform(post("/user/testUser")
                        .header("token", "validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockTransactionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.symbol").value("BTCUSDT"))
                .andExpect(jsonPath("$.amount").value("0.5"))
                .andExpect(jsonPath("$.type").value("BUY"));
    }

    @Test
    void performTrade_shouldReturn400_whenTokenIsMissing() throws Exception {
        mockMvc.perform(post("/user/testUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockTransactionDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWallets_shouldReturnWalletDtoPage() throws Exception {
        when(userService.getWallets(eq("testUser"), anyInt(), anyInt())).thenReturn(mockWalletDtoPage);

        mockMvc.perform(get("/user/testUser/wallets")
                        .header("token", "validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pages").value(1))
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void getTransactions_shouldReturnTransactionDtoPage() throws Exception {
        when(userService.getTransactions(eq("testUser"), anyInt(), anyInt())).thenReturn(mockTransactionDtoPage);

        mockMvc.perform(get("/user/testUser/transactions")
                        .header("token", "validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pages").value(1))
                .andExpect(jsonPath("$.total").value(0));
    }
}