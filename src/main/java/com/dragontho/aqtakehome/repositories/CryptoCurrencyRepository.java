package com.dragontho.aqtakehome.repositories;

import com.dragontho.aqtakehome.models.CryptoCurrency;
import org.springframework.data.repository.CrudRepository;

public interface CryptoCurrencyRepository extends CrudRepository<CryptoCurrency, Long> {
    CryptoCurrency findBySymbol(String symbol);
}
