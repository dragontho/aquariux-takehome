package com.dragontho.aqtakehome.data.internapi;

import com.dragontho.aqtakehome.enums.TransactionType;
import com.dragontho.aqtakehome.models.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {
    @JsonProperty("username")
    private String username;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("type")
    private TransactionType type;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;


    public static TransactionDto fromModel(Transaction model) {
        TransactionDto dto = new TransactionDto();
        dto.setUsername(model.getUser().getUsername());
        dto.setSymbol(model.getCurrencyPair().getSymbol());
        dto.setType(model.getType());
        dto.setAmount(model.getAmount());
        dto.setPrice(model.getPrice());
        dto.setTimestamp(model.getTimestamp());
        return dto;
    }
}
