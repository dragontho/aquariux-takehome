package com.dragontho.aqtakehome.data.internapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WalletDtoPage {
    @JsonProperty("data")
    private List<WalletDto> data;

    @JsonProperty("pages")
    private int pages;

    @JsonProperty("total")
    private long total;
}
