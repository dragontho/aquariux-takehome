package com.dragontho.aqtakehome.api;

import com.dragontho.aqtakehome.annotations.ApiClient;
import com.dragontho.aqtakehome.data.BinanceTicker;
import com.dragontho.aqtakehome.data.HuobiTicker;
import com.dragontho.aqtakehome.data.HuobiTickerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ApiClient
@Slf4j
public class HuobiApiImpl {
    private final WebClient webClient;

    public HuobiApiImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.huobi.pro").build();
    }

    public Mono<HuobiTickerResponse> getTickers() {
        return this.webClient.get()
                .uri("/market/tickers")
                .retrieve()
                .bodyToMono(HuobiTickerResponse.class);
    }
}
