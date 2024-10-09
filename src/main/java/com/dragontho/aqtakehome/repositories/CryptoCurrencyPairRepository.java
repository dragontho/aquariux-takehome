package com.dragontho.aqtakehome.repositories;

import com.dragontho.aqtakehome.models.CryptoCurrencyPair;
import org.springframework.data.repository.CrudRepository;

public interface CryptoCurrencyPairRepository extends CrudRepository<CryptoCurrencyPair, Long> {
    CryptoCurrencyPair findBySymbol(String symbol);
}
