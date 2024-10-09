package com.dragontho.aqtakehome.controllers;

import com.dragontho.aqtakehome.data.internapi.AggregatedPriceDto;
import com.dragontho.aqtakehome.exceptions.InternalException;
import com.dragontho.aqtakehome.services.ExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceController.class)
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeService exchangeService;

    private AggregatedPriceDto mockBtcPrice;
    private AggregatedPriceDto mockEthPrice;

    @BeforeEach
    void setUp() {
        mockBtcPrice = new AggregatedPriceDto();
        mockBtcPrice.setSymbol("BTCUSDT");
        mockBtcPrice.setBidPrice(BigDecimal.valueOf(50000.0));
        mockBtcPrice.setAskPrice(BigDecimal.valueOf(50100.0));
        mockBtcPrice.setBidPriceSource("binance");
        mockBtcPrice.setAskPriceSource("huobi");
        mockBtcPrice.setTimestamp(LocalDateTime.now());

        mockEthPrice = new AggregatedPriceDto();
        mockEthPrice.setSymbol("ETHUSDT");
        mockEthPrice.setBidPrice(BigDecimal.valueOf(3000.0));
        mockEthPrice.setAskPrice(BigDecimal.valueOf(3010.0));
        mockEthPrice.setBidPriceSource("huobi");
        mockEthPrice.setAskPriceSource("binance");
        mockEthPrice.setTimestamp(LocalDateTime.now());
    }

    @Test
    void getLatestAggregatedPrice_withoutSymbol_shouldReturnAllPrices() throws Exception {
        List<AggregatedPriceDto> mockPrices = Arrays.asList(mockBtcPrice, mockEthPrice);
        when(exchangeService.getLatestAggregatedPrices()).thenReturn(mockPrices);

        mockMvc.perform(get("/prices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].symbol").value("BTCUSDT"))
                .andExpect(jsonPath("$[1].symbol").value("ETHUSDT"));

        verify(exchangeService).getLatestAggregatedPrices();
    }

    @Test
    void getLatestAggregatedPrice_withSymbol_shouldReturnSinglePrice() throws Exception {
        when(exchangeService.getLatestAggregatedPrice("BTCUSDT")).thenReturn(mockBtcPrice);

        mockMvc.perform(get("/prices").param("symbol", "BTCUSDT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].symbol").value("BTCUSDT"))
                .andExpect(jsonPath("$[0].bidPrice").value("50000.0"))
                .andExpect(jsonPath("$[0].askPrice").value("50100.0"));

        verify(exchangeService).getLatestAggregatedPrice("BTCUSDT");
    }

    @Test
    void getLatestAggregatedPrice_withInvalidSymbol_shouldReturnNotFound() throws Exception {
        when(exchangeService.getLatestAggregatedPrice("INVALIDUSDT"))
                .thenThrow(new InternalException("No available prices found for symbol INVALIDUSDT"));

        mockMvc.perform(get("/prices").param("symbol", "INVALIDUSDT"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.body.type").value("about:blank"))
                .andExpect(jsonPath("$.body.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.body.status").value(500))
                .andExpect(jsonPath("$.body.detail").value("No available prices found for symbol INVALIDUSDT"));
    }

    @Test
    void getLatestAggregatedPrice_whenServiceThrowsException_shouldReturnInternalServerError() throws Exception {
        when(exchangeService.getLatestAggregatedPrices())
                .thenThrow(new InternalException("Internal server error"));

        mockMvc.perform(get("/prices"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.body.type").value("about:blank"))
                .andExpect(jsonPath("$.body.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.body.status").value(500))
                .andExpect(jsonPath("$.body.detail").value("Internal server error"));
    }
}
