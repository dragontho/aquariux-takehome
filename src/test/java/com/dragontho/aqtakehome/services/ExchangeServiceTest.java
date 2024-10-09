package com.dragontho.aqtakehome.services;

import com.dragontho.aqtakehome.api.BinanceApiClient;
import com.dragontho.aqtakehome.api.HuobiApiClient;
import com.dragontho.aqtakehome.data.externapi.BinanceTicker;
import com.dragontho.aqtakehome.data.externapi.HuobiTicker;
import com.dragontho.aqtakehome.data.externapi.HuobiTickerResponse;
import com.dragontho.aqtakehome.data.internapi.AggregatedPriceDto;
import com.dragontho.aqtakehome.models.AggregatedPrice;
import com.dragontho.aqtakehome.models.CryptoCurrencyPair;
import com.dragontho.aqtakehome.repositories.AggregatedPriceRepository;
import com.dragontho.aqtakehome.repositories.CryptoCurrencyPairRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeServiceTest {

    @Mock
    private BinanceApiClient binanceApiClient;

    @Mock
    private HuobiApiClient huobiApiClient;

    @Mock
    private AggregatedPriceRepository aggregatedPriceRepository;

    @Mock
    private CryptoCurrencyPairRepository cryptoCurrencyPairRepository;

    @Mock
    private ThreadPoolTaskExecutor taskExecutor;

    @InjectMocks
    private ExchangeService exchangeService;

    private AggregatedPrice mockAggregatedPrice;

    @BeforeEach
    void setUp() {
        CryptoCurrencyPair mockCryptoCurrencyPair = new CryptoCurrencyPair();
        mockCryptoCurrencyPair.setSymbol("BTCUSDT");

        mockAggregatedPrice = new AggregatedPrice();
        mockAggregatedPrice.setCurrencyPair(mockCryptoCurrencyPair);
        mockAggregatedPrice.setBidPrice(new BigDecimal("50000.00"));
        mockAggregatedPrice.setAskPrice(new BigDecimal("50100.00"));
        mockAggregatedPrice.setBidPriceSource("binance");
        mockAggregatedPrice.setAskPriceSource("huobi");
        mockAggregatedPrice.setTimestamp(LocalDateTime.now());
    }

    @Test
    void getLatestAggregatedPrices_shouldReturnPrices_whenPricesExist() throws Exception {
        when(aggregatedPriceRepository.findLatestPricesForAllPairs())
                .thenReturn(Collections.singletonList(mockAggregatedPrice));

        List<AggregatedPriceDto> result =
                exchangeService.getLatestAggregatedPrices();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("BTCUSDT", result.get(0).getSymbol());
        assertEquals(new BigDecimal("50000.00"), result.get(0).getBidPrice());
        assertEquals(new BigDecimal("50100.00"), result.get(0).getAskPrice());
    }

    @Test
    void getLatestAggregatedPrices_shouldThrowException_whenNoPricesExist() {
        when(aggregatedPriceRepository.findLatestPricesForAllPairs())
                .thenReturn(Collections.emptyList());

        Exception exception = assertThrows(Exception.class, () -> exchangeService.getLatestAggregatedPrices());

        assertEquals("No available prices found for any symbol", exception.getMessage());
    }

    @Test
    void getLatestAggregatedPrice_shouldReturnPrice_whenPriceExists() throws Exception {
        when(aggregatedPriceRepository.findTopByCurrencyPair_SymbolOrderByTimestampDesc("BTCUSDT"))
                .thenReturn(Optional.of(mockAggregatedPrice));

        AggregatedPriceDto result =
                exchangeService.getLatestAggregatedPrice("BTCUSDT");

        assertNotNull(result);
        assertEquals("BTCUSDT", result.getSymbol());
        assertEquals(new BigDecimal("50000.00"), result.getBidPrice());
        assertEquals(new BigDecimal("50100.00"), result.getAskPrice());
    }

    @Test
    void getLatestAggregatedPrice_shouldThrowException_whenNoPriceExists() {
        when(aggregatedPriceRepository.findTopByCurrencyPair_SymbolOrderByTimestampDesc("ETHUSD"))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> exchangeService.getLatestAggregatedPrice("ETHUSD"));

        assertEquals("No available prices found for symbol ETHUSD", exception.getMessage());
    }

    @Test
    void aggregatePrices_shouldAggregateAndSavePrices() {
        BinanceTicker binanceEthTicker = new BinanceTicker();
        binanceEthTicker.setSymbol("ETHUSDT");
        binanceEthTicker.setBidPrice("3000.00");
        binanceEthTicker.setAskPrice("3001.00");

        BinanceTicker binanceBtcTicker = new BinanceTicker();
        binanceBtcTicker.setSymbol("BTCUSDT");
        binanceBtcTicker.setBidPrice("50000.00");
        binanceBtcTicker.setAskPrice("50001.00");

        BinanceTicker[] mockBinanceTickers = new BinanceTicker[]{binanceEthTicker, binanceBtcTicker};

        HuobiTicker huobiEthTicker = new HuobiTicker();
        huobiEthTicker.setSymbol("ethusdt");
        huobiEthTicker.setBid(new BigDecimal("2999.00"));
        huobiEthTicker.setAsk(new BigDecimal("3002.00"));

        HuobiTicker huobiBtcTicker = new HuobiTicker();
        huobiBtcTicker.setSymbol("btcusdt");
        huobiBtcTicker.setBid(new BigDecimal("49999.00"));
        huobiBtcTicker.setAsk(new BigDecimal("50002.00"));

        HuobiTickerResponse mockHuobiResponse = new HuobiTickerResponse();
        mockHuobiResponse.setData(new HuobiTicker[]{huobiEthTicker, huobiBtcTicker});

        when(binanceApiClient.getTickers()).thenReturn(Mono.just(mockBinanceTickers));
        when(huobiApiClient.getTickers()).thenReturn(Mono.just(mockHuobiResponse));

        CryptoCurrencyPair ethPair = new CryptoCurrencyPair();
        ethPair.setSymbol("ETHUSDT");
        CryptoCurrencyPair btcPair = new CryptoCurrencyPair();
        btcPair.setSymbol("BTCUSDT");

        when(cryptoCurrencyPairRepository.findBySymbol("ETHUSDT")).thenReturn(ethPair);
        when(cryptoCurrencyPairRepository.findBySymbol("BTCUSDT")).thenReturn(btcPair);
        when(aggregatedPriceRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        // Mock ThreadPoolTaskExecutor to run tasks immediately
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        exchangeService.aggregatePrices().join();

        verify(aggregatedPriceRepository, times(1)).saveAll(anyList());

        verify(cryptoCurrencyPairRepository, times(1)).findBySymbol("ETHUSDT");
        verify(cryptoCurrencyPairRepository, times(1)).findBySymbol("BTCUSDT");
    }
}
