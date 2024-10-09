package com.dragontho.aqtakehome.data.internapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AggregatedPriceDto {
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


    public static AggregatedPriceDto fromModel(com.dragontho.aqtakehome.models.AggregatedPrice model) {
        AggregatedPriceDto aggregatedPriceDto = new AggregatedPriceDto();
        aggregatedPriceDto.setSymbol(model.getCurrencyPair().getSymbol());
        aggregatedPriceDto.setBidPrice(model.getBidPrice());
        aggregatedPriceDto.setBidPriceSource(model.getBidPriceSource());
        aggregatedPriceDto.setAskPrice(model.getAskPrice());
        aggregatedPriceDto.setAskPriceSource(model.getAskPriceSource());
        aggregatedPriceDto.setTimestamp(model.getTimestamp());
        return aggregatedPriceDto;
    }
}
