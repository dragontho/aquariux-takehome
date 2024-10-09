package com.dragontho.aqtakehome.data.internapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AggregatedPrice {
    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("bidPrice")
    private BigDecimal bidPrice;

    @JsonProperty("bidPriceSource")
    private String bidPriceSource;

    @JsonProperty("askPrice")
    private BigDecimal askPrice;

    @JsonProperty("askPriceSource")
    private String askPriceSource;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;


    public static AggregatedPrice fromModel(com.dragontho.aqtakehome.models.AggregatedPrice model) {
        AggregatedPrice aggregatedPrice = new AggregatedPrice();
        aggregatedPrice.setSymbol(model.getCurrencyPair().getSymbol());
        aggregatedPrice.setBidPrice(model.getBidPrice());
        aggregatedPrice.setBidPriceSource(model.getBidPriceSource());
        aggregatedPrice.setAskPrice(model.getAskPrice());
        aggregatedPrice.setAskPriceSource(model.getAskPriceSource());
        aggregatedPrice.setTimestamp(model.getTimestamp());
        return aggregatedPrice;
    }
}
