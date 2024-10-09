package com.dragontho.aqtakehome.services;

import com.dragontho.aqtakehome.data.internapi.TransactionDto;
import com.dragontho.aqtakehome.data.internapi.WalletDto;
import com.dragontho.aqtakehome.data.internapi.WalletDtoPage;
import com.dragontho.aqtakehome.enums.TransactionType;
import com.dragontho.aqtakehome.models.*;
import com.dragontho.aqtakehome.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserService {
    private final CryptoCurrencyPairRepository cryptoCurrencyPairRepository;
    private final AggregatedPriceRepository aggregatedPriceRepository;
    private UserRepository userRepository;
    private TransactionRepository transactionRepository;
    private WalletRepository walletRepository;

    @Autowired
    public UserService(TransactionRepository transactionRepository,
                       UserRepository userRepository,
                       WalletRepository walletRepository, CryptoCurrencyPairRepository cryptoCurrencyPairRepository, AggregatedPriceRepository aggregatedPriceRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.cryptoCurrencyPairRepository = cryptoCurrencyPairRepository;
        this.aggregatedPriceRepository = aggregatedPriceRepository;
    }

    public WalletDtoPage getWallets(String username, int page, int size) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Page<Wallet> wallets = walletRepository.findByUserId(user.getId(), PageRequest.of(page, size));
        int totalPages = wallets.getTotalPages();
        long totalElements = wallets.getTotalElements();
        List<WalletDto> walletDtos = wallets.stream().map(WalletDto::fromModel).toList();
        WalletDtoPage walletDtoPage = new WalletDtoPage();
        walletDtoPage.setData(walletDtos);
        walletDtoPage.setPages(totalPages);
        walletDtoPage.setTotal(totalElements);
        return walletDtoPage;
    }

    @Transactional
    public TransactionDto executeTrade(TransactionDto transaction) {
        return TransactionDto.fromModel(executeTrade(transaction.getUsername(),
                transaction.getSymbol(), transaction.getAmount(), transaction.getType()));
    }

    private Transaction executeTrade(String username, String symbol,
                                     BigDecimal amount, TransactionType type) {
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            AggregatedPrice latestBestPrice = aggregatedPriceRepository.findTopByCurrencyPair_SymbolOrderByTimestampDesc(symbol).orElseThrow();
            CryptoCurrencyPair currencyPair = cryptoCurrencyPairRepository.findBySymbol(symbol);
            BigDecimal price = Objects.equals(type, TransactionType.BUY) ? latestBestPrice.getBidPrice() : latestBestPrice.getAskPrice();
            // Insert the transaction record
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setCurrencyPair(currencyPair);
            transaction.setType(type);
            transaction.setAmount(amount);
            transaction.setPrice(price);
            transaction.setTimestamp(LocalDateTime.now());
            transactionRepository.save(transaction);

            CryptoCurrency fromWalletCurrency = Objects.equals(type, TransactionType.SELL)
                    ? currencyPair.getFirstCurrency()
                    : currencyPair.getSecondCurrency();
            CryptoCurrency toWalletCurrency = Objects.equals(type, TransactionType.BUY)
                    ? currencyPair.getFirstCurrency()
                    : currencyPair.getSecondCurrency();

            // Decrease `from` wallet balance
            Wallet fromWallet = walletRepository.findByUserIdAndCurrency(user.getId(), fromWalletCurrency).orElseGet(() -> {
                Wallet newWallet = new Wallet();
                newWallet.setUser(user);
                newWallet.setBalance(BigDecimal.ZERO);
                newWallet.setCurrency(fromWalletCurrency);
                return newWallet;
            });
            BigDecimal sentAmount;
            if (TransactionType.BUY.equals(transaction.getType())) {
                sentAmount = amount.multiply(price);
            } else {
                sentAmount = amount;
            }
            fromWallet.setBalance(fromWallet.getBalance().subtract(sentAmount));
            if (fromWallet.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("Not enough balance");
            }
            walletRepository.save(fromWallet);

            // Increase `to` wallet balance
            Wallet toWallet = walletRepository.findByUserIdAndCurrency(user.getId(), toWalletCurrency).orElseGet(() -> {
                Wallet newWallet = new Wallet();
                newWallet.setUser(user);
                newWallet.setBalance(BigDecimal.ZERO);
                newWallet.setCurrency(toWalletCurrency);
                return newWallet;
            });
            BigDecimal receivedAmount;
            if (TransactionType.SELL.equals(transaction.getType())) {
                receivedAmount = amount.multiply(price);
            } else {
                receivedAmount = amount;
            }
            toWallet.setBalance(toWallet.getBalance().add(receivedAmount));
            walletRepository.save(toWallet);

            return transaction;
        } catch (Exception e) {
            log.error("Error executing trade: ", e);
            throw new RuntimeException("Failed to execute trade", e);
        }
    }

}
