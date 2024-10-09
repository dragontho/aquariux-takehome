package com.dragontho.aqtakehome.repositories;

import com.dragontho.aqtakehome.models.Transaction;
import org.springframework.data.repository.ListCrudRepository;

public interface TransactionRepository extends ListCrudRepository<Transaction, Long> {
}
