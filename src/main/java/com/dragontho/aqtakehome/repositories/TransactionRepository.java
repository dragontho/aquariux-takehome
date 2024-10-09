package com.dragontho.aqtakehome.repositories;

import com.dragontho.aqtakehome.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransactionRepository extends ListCrudRepository<Transaction, Long>, PagingAndSortingRepository<Transaction, Long> {
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
}
