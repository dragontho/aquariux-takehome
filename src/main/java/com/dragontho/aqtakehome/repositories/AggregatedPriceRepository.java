package com.dragontho.aqtakehome.repositories;

import com.dragontho.aqtakehome.models.AggregatedPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AggregatedPriceRepository extends JpaRepository<AggregatedPrice, Long> {
    List<AggregatedPrice> findTopByCurrencyPair_SymbolOrderByTimestampDesc(String symbol);

    @Query("SELECT ap FROM AggregatedPrice ap " +
            "WHERE ap.timestamp = (" +
            "SELECT MAX(ap2.timestamp) " +
            "FROM AggregatedPrice ap2 " +
            "WHERE ap2.currencyPair = ap.currencyPair" +
            ")")
    List<AggregatedPrice> findLatestPricesForAllPairs();
}
