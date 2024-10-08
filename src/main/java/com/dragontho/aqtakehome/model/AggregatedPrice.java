package com.dragontho.aqtakehome.model;

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
    @JoinColumn(name = "currency_id", nullable = false)
    private CryptoCurrency currency;

    @Column(nullable = false)
    private BigDecimal bidPrice;

    @Column(nullable = false)
    private String bidPriceSource;

    @Column(nullable = false)
    private BigDecimal askPrice;

    @Column(nullable = false)
    private String askPriceSource;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}