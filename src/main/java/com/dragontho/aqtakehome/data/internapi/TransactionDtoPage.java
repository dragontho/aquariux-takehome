package com.dragontho.aqtakehome.data.internapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TransactionDtoPage {
    @JsonProperty("data")
    private List<TransactionDto> data;

    @JsonProperty("pages")
    private int pages;

    @JsonProperty("total")
    private long total;
}
