package com.dragontho.aqtakehome.repositories;

import com.dragontho.aqtakehome.models.CryptoCurrency;
import com.dragontho.aqtakehome.models.Wallet;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface WalletRepository extends ListCrudRepository<Wallet, Long> {
    Optional<Wallet> findByUserIdAndCurrency(Long userId, CryptoCurrency currency);
}
