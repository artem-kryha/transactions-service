package ua.kryha.transactionsservice.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import ua.kryha.transactionsservice.entity.BlockTrackerEntity;
import ua.kryha.transactionsservice.repository.BlockTrackerRepository;

@Service
@RequiredArgsConstructor
public class BlockTrackerService {

    private final BlockTrackerRepository blockTrackerRepository;

    public Long getLastProcessedBlock() {
        return blockTrackerRepository.findById("lastProcessedBlock")
                .map(BlockTrackerEntity::getBlockNumber)
                .orElse(0L);
    }

    public void updateLastProcessedBlock(Long blockNumber) {
        blockTrackerRepository.updateLastProcessedBlock("lastProcessedBlock", blockNumber);
    }
}
