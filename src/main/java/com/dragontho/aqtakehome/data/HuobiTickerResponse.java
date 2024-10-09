package com.dragontho.aqtakehome.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HuobiTickerResponse {
    @JsonProperty("data")
    private HuobiTicker[] data;
}
