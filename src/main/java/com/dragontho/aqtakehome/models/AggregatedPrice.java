package com.dragontho.aqtakehome.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "aggregated_prices")
public class AggregatedPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pair_id", nullable = false)
    private CryptoCurrencyPair currencyPair;

    @Column(nullable = false, precision = 38, scale = 8)
    private BigDecimal bidPrice;

    @Column(nullable = false)
    private String bidPriceSource;

    @Column(nullable = false, precision = 38, scale = 8)
    private BigDecimal askPrice;

    @Column(nullable = false)
    private String askPriceSource;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}