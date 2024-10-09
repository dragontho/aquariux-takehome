package com.dragontho.aqtakehome.services;

import com.dragontho.aqtakehome.api.BinanceApiClient;
import com.dragontho.aqtakehome.api.HuobiApiClient;
import com.dragontho.aqtakehome.data.externapi.BinanceTicker;
import com.dragontho.aqtakehome.data.externapi.HuobiTicker;
import com.dragontho.aqtakehome.data.externapi.HuobiTickerResponse;
import com.dragontho.aqtakehome.data.internapi.AggregatedPriceDto;
import com.dragontho.aqtakehome.exceptions.InternalException;
import com.dragontho.aqtakehome.models.AggregatedPrice;
import com.dragontho.aqtakehome.repositories.AggregatedPriceRepository;
import com.dragontho.aqtakehome.repositories.CryptoCurrencyPairRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExchangeService {
    private final BinanceApiClient binanceApiClient;
    private final HuobiApiClient huobiApiClient;
    private final AggregatedPriceRepository aggregatedPriceRepository;
    private final CryptoCurrencyPairRepository cryptoCurrencyPairRepository;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    public ExchangeService(BinanceApiClient binanceApiClient,
                           HuobiApiClient huobiApiClient,
                           AggregatedPriceRepository aggregatedPriceRepository,
                           CryptoCurrencyPairRepository cryptoCurrencyPairRepository,
                           @Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor) {
        this.binanceApiClient = binanceApiClient;
        this.huobiApiClient = huobiApiClient;
        this.aggregatedPriceRepository = aggregatedPriceRepository;
        this.cryptoCurrencyPairRepository = cryptoCurrencyPairRepository;
        this.taskExecutor = taskExecutor;
    }

    public List<AggregatedPriceDto> getLatestAggregatedPrices() throws InternalException {
        List<AggregatedPrice> aggregatedPrices = aggregatedPriceRepository.findLatestPricesForAllPairs();
        if (aggregatedPrices.isEmpty()) {
            throw new InternalException("No available prices found for any symbol");
        }
        return aggregatedPrices.stream().map(AggregatedPriceDto::fromModel).collect(Collectors.toList());
    }

    public AggregatedPriceDto getLatestAggregatedPrice(String symbol) throws InternalException {
        String symbolUpper = symbol.toUpperCase();
        Optional<AggregatedPrice> aggregatedPrice = aggregatedPriceRepository
                .findTopByCurrencyPair_SymbolOrderByTimestampDesc(symbolUpper);
        if (aggregatedPrice.isEmpty()) {
            throw new InternalException("No available prices found for symbol " + symbolUpper);
        }
        return AggregatedPriceDto.fromModel(aggregatedPrice.get());
    }

    public CompletableFuture<Void> aggregatePrices() {
        Set<String> symbols = Set.of("ETHUSDT", "BTCUSDT");
        return CompletableFuture.supplyAsync(() -> aggregatePriceForSymbols(symbols), taskExecutor).thenAccept(result -> {
            if (result != null) {
                log.info("Price aggregation completed at: {}", new Date());
            }
        });
    }

    private List<AggregatedPrice> aggregatePriceForSymbols(Set<String> symbols) {
        CompletableFuture<BinanceTicker[]> binanceFuture = binanceApiClient.getTickers().toFuture();

        CompletableFuture<HuobiTickerResponse> huobiFuture = huobiApiClient.getTickers().toFuture();

        return CompletableFuture.allOf(binanceFuture, huobiFuture).thenApplyAsync(v -> {
            BinanceTicker[] binanceResp = binanceFuture.join();
            HuobiTickerResponse huobiResp = huobiFuture.join();
            Map<String, BinanceTicker> binanceTickerMap = Arrays.stream(binanceResp)
                    .filter(s -> symbols.contains(s.getSymbol().toUpperCase()))
                    .collect(Collectors.toMap(bt -> bt.getSymbol().toUpperCase(), bt -> bt));
            Map<String, HuobiTicker> huobiTickerMap = Arrays.stream(huobiResp.getData())
                    .filter(s -> symbols.contains(s.getSymbol().toUpperCase()))
                    .collect(Collectors.toMap(ht -> ht.getSymbol().toUpperCase(), ht -> ht));

            List<AggregatedPrice> aggregatedPrices = symbols.stream()
                    .map(symbol -> aggregatePriceForSymbol(symbol, binanceTickerMap, huobiTickerMap))
                    .collect(Collectors.toList());

            log.debug(aggregatedPrices.toString());
            aggregatedPriceRepository.saveAll(aggregatedPrices);
            return aggregatedPrices;
        }, taskExecutor).join();
    }

    private AggregatedPrice aggregatePriceForSymbol(String symbol,
                                                    Map<String, BinanceTicker> binanceTickerMap,
                                                    Map<String, HuobiTicker> huobiTickerMap) {
        BinanceTicker binanceTicker = binanceTickerMap.get(symbol);
        HuobiTicker huobiTicker = huobiTickerMap.get(symbol);

        BigDecimal binanceBid = new BigDecimal(binanceTicker.getBidPrice());
        BigDecimal binanceAsk = new BigDecimal(binanceTicker.getAskPrice());
        BigDecimal bestBidPrice = binanceBid.compareTo(huobiTicker.getBid()) > 0 ? binanceBid : huobiTicker.getBid();
        BigDecimal bestAskPrice = binanceAsk.compareTo(huobiTicker.getAsk()) < 0 ? binanceAsk : huobiTicker.getAsk();
        String bestBidPriceSource = binanceBid.compareTo(huobiTicker.getBid()) > 0 ? "binance" : "huobi";
        String bestAskPriceSource = binanceAsk.compareTo(huobiTicker.getAsk()) < 0 ? "binance" : "huobi";

        AggregatedPrice aggregatedPrice = new AggregatedPrice();
        aggregatedPrice.setCurrencyPair(cryptoCurrencyPairRepository.findBySymbol(symbol));
        aggregatedPrice.setAskPrice(bestAskPrice);
        aggregatedPrice.setBidPrice(bestBidPrice);
        aggregatedPrice.setAskPriceSource(bestAskPriceSource);
        aggregatedPrice.setBidPriceSource(bestBidPriceSource);
        aggregatedPrice.setTimestamp(LocalDateTime.now());

        return aggregatedPrice;
    }

}
