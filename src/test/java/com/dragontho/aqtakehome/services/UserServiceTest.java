package com.dragontho.aqtakehome.services;
import com.dragontho.aqtakehome.data.internapi.TransactionDto;
import com.dragontho.aqtakehome.data.internapi.TransactionDtoPage;
import com.dragontho.aqtakehome.data.internapi.WalletDtoPage;
import com.dragontho.aqtakehome.enums.TransactionType;
import com.dragontho.aqtakehome.models.*;
import com.dragontho.aqtakehome.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private CryptoCurrencyPairRepository cryptoCurrencyPairRepository;
    @Mock
    private AggregatedPriceRepository aggregatedPriceRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private Wallet mockWallet;
    private Transaction mockTransaction;
    private CryptoCurrencyPair mockCurrencyPair;
    private AggregatedPrice mockAggregatedPrice;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");

        CryptoCurrency btc = new CryptoCurrency();
        btc.setSymbol("BTC");
        CryptoCurrency usdt = new CryptoCurrency();
        usdt.setSymbol("USDT");

        mockWallet = new Wallet();
        mockWallet.setUser(mockUser);
        mockWallet.setCurrency(btc);
        mockWallet.setBalance(new BigDecimal("1.5"));

        mockCurrencyPair = new CryptoCurrencyPair();
        mockCurrencyPair.setSymbol("BTCUSDT");
        mockCurrencyPair.setFirstCurrency(btc);
        mockCurrencyPair.setSecondCurrency(usdt);

        mockTransaction = new Transaction();
        mockTransaction.setUser(mockUser);
        mockTransaction.setCurrencyPair(mockCurrencyPair);
        mockTransaction.setType(TransactionType.BUY);
        mockTransaction.setAmount(new BigDecimal("0.5"));
        mockTransaction.setPrice(new BigDecimal("50000"));
        mockTransaction.setTimestamp(LocalDateTime.now());

        mockAggregatedPrice = new AggregatedPrice();
        mockAggregatedPrice.setCurrencyPair(mockCurrencyPair);
        mockAggregatedPrice.setBidPrice(new BigDecimal("50000"));
        mockAggregatedPrice.setAskPrice(new BigDecimal("50100"));
    }

    @Test
    void getWallets_shouldReturnWalletDtoPage() {
        Page<Wallet> walletPage = new PageImpl<>(Collections.singletonList(mockWallet));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(walletRepository.findByUserId(1L, PageRequest.of(0, 10))).thenReturn(walletPage);

        WalletDtoPage result = userService.getWallets("testUser", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals(1, result.getPages());
        assertEquals(1, result.getTotal());
        assertEquals("BTC", result.getData().get(0).getSymbol());
        assertEquals(new BigDecimal("1.5"), result.getData().get(0).getBalance());
    }

    @Test
    void getTransactions_shouldReturnTransactionDtoPage() {
        Page<Transaction> transactionPage = new PageImpl<>(Collections.singletonList(mockTransaction));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(transactionRepository.findByUserId(1L, PageRequest.of(0, 10))).thenReturn(transactionPage);

        TransactionDtoPage result = userService.getTransactions("testUser", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals(1, result.getPages());
        assertEquals(1, result.getTotal());
        assertEquals("BTCUSDT", result.getData().get(0).getSymbol());
        assertEquals(TransactionType.BUY, result.getData().get(0).getType());
    }

    @Test
    void executeTrade_shouldExecuteBuyTrade() {
        TransactionDto inputDto = new TransactionDto();
        inputDto.setUsername("testUser");
        inputDto.setSymbol("BTCUSDT");
        inputDto.setAmount(new BigDecimal("0.5"));
        inputDto.setType(TransactionType.BUY);

        CryptoCurrency usdt = new CryptoCurrency();
        usdt.setSymbol("USDT");
        Wallet usdtWallet = new Wallet();
        usdtWallet.setUser(mockUser);
        usdtWallet.setCurrency(usdt);
        usdtWallet.setBalance(new BigDecimal("300000"));

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(aggregatedPriceRepository.findTopByCurrencyPair_SymbolOrderByTimestampDesc("BTCUSDT"))
                .thenReturn(Optional.of(mockAggregatedPrice));
        when(cryptoCurrencyPairRepository.findBySymbol("BTCUSDT")).thenReturn(mockCurrencyPair);
        when(walletRepository.findByUserIdAndCurrency(1L, mockCurrencyPair.getSecondCurrency()))
                .thenReturn(Optional.of(usdtWallet));
        when(walletRepository.findByUserIdAndCurrency(1L, mockCurrencyPair.getFirstCurrency()))
                .thenReturn(Optional.of(mockWallet));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        TransactionDto result = userService.executeTrade(inputDto);

        assertNotNull(result);
        assertEquals("BTCUSDT", result.getSymbol());
        assertEquals(TransactionType.BUY, result.getType());
        assertEquals(new BigDecimal("0.5"), result.getAmount());
        verify(walletRepository, times(2)).save(any(Wallet.class));
    }

    @Test
    void executeTrade_shouldThrowException_whenInsufficientBalance() {
        TransactionDto inputDto = new TransactionDto();
        inputDto.setUsername("testUser");
        inputDto.setSymbol("BTCUSDT");
        inputDto.setAmount(new BigDecimal("100")); // Large amount to trigger insufficient balance
        inputDto.setType(TransactionType.SELL);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(aggregatedPriceRepository.findTopByCurrencyPair_SymbolOrderByTimestampDesc("BTCUSDT"))
                .thenReturn(Optional.of(mockAggregatedPrice));
        when(cryptoCurrencyPairRepository.findBySymbol("BTCUSDT")).thenReturn(mockCurrencyPair);
        when(walletRepository.findByUserIdAndCurrency(1L, mockCurrencyPair.getFirstCurrency()))
                .thenReturn(Optional.of(mockWallet));

        assertThrows(RuntimeException.class, () -> userService.executeTrade(inputDto));
    }
}