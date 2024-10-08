package com.dragontho.aqtakehome.api;

import com.dragontho.aqtakehome.annotations.ApiClient;
import com.dragontho.aqtakehome.data.externapi.BinanceTicker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ApiClient
@Slf4j
public class BinanceApiClient {
    private final WebClient webClient;

    public BinanceApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.binance.com/api/v3").build();
    }

    public Mono<BinanceTicker[]> getTickers() {
        return this.webClient.get()
                .uri("/ticker/bookTicker")
                .retrieve()
                .bodyToMono(BinanceTicker[].class);
    }
}
