package com.dragontho.aqtakehome.api;

import com.dragontho.aqtakehome.annotations.ApiClient;
import com.dragontho.aqtakehome.data.HuobiTickerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ApiClient
@Slf4j
public class HuobiApiClient {
    private final WebClient webClient;

    public HuobiApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.huobi.pro").build();
    }

    public Mono<HuobiTickerResponse> getTickers() {
        return this.webClient.get()
                .uri("/market/tickers")
                .retrieve()
                .bodyToMono(HuobiTickerResponse.class);
    }
}
