package com.dragontho.aqtakehome.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BinanceTicker {
    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("bidPrice")
    private String bidPrice;

    @JsonProperty("askPrice")
    private String askPrice;
}
