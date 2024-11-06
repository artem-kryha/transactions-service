package ua.kryha.transactionsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.kryha.transactionsservice.entity.TransactionEntity;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByTransactionHashStartingWith(String prefix);
}
