package com.dragontho.aqtakehome.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cryptocurrencypairs")
public class CryptoCurrencyPair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String symbol;

    @ManyToOne
    @JoinColumn(name = "first_currency_id", nullable = false)
    private CryptoCurrency firstCurrency;

    @ManyToOne
    @JoinColumn(name = "second_currency_id", nullable = false)
    private CryptoCurrency secondCurrency;
}