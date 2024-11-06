package ua.kryha.transactionsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ua.kryha.transactionsservice.entity.BlockTrackerEntity;

@Repository
public interface BlockTrackerRepository extends JpaRepository<BlockTrackerEntity, String> {
    @Modifying
    @Transactional
    @Query("UPDATE BlockTrackerEntity b SET b.blockNumber = :blockNumber WHERE b.id = :id")
    void updateLastProcessedBlock(String id, Long blockNumber);
}
