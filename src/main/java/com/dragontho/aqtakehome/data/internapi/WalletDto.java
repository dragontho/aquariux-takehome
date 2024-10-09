package com.dragontho.aqtakehome.data.internapi;

import com.dragontho.aqtakehome.models.Wallet;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletDto {
    @JsonProperty("username")
    private String username;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("currencyName")
    private String currencyName;

    @JsonProperty("balance")
    private BigDecimal balance;

    public static WalletDto fromModel(Wallet wallet) {
        WalletDto dto = new WalletDto();
        dto.setUsername(wallet.getUser().getUsername());
        dto.setSymbol(wallet.getCurrency().getSymbol());
        dto.setCurrencyName(wallet.getCurrency().getName());
        dto.setBalance(wallet.getBalance());
        return dto;
    }
}
