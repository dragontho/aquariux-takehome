package com.dragontho.aqtakehome.repositories;

import com.dragontho.aqtakehome.models.CryptoCurrency;
import com.dragontho.aqtakehome.models.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface WalletRepository extends ListCrudRepository<Wallet, Long>, PagingAndSortingRepository<Wallet, Long> {
    Optional<Wallet> findByUserIdAndCurrency(Long userId, CryptoCurrency currency);
    Page<Wallet> findByUserId(Long userId, Pageable pageable);
}
