package ua.kryha.transactionsservice.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.EthBlock;

import ua.kryha.transactionsservice.entity.TransactionEntity;
import ua.kryha.transactionsservice.repository.TransactionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BlockTrackerService blockTrackerService;

    @Transactional
    public void processBlock(EthBlock.Block block) {
        List<TransactionEntity> transactionsToSave = buildTransactionEntities(block);
        transactionRepository.saveAll(transactionsToSave);

        blockTrackerService.updateLastProcessedBlock(block.getNumber().longValue());
    }

    public List<TransactionEntity> buildTransactionEntities(EthBlock.Block block) {
        return block.getTransactions().stream()
                .map(tx -> {
                    EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
                    TransactionEntity entity = new TransactionEntity();
                    entity.setTransactionHash(transaction.getHash());
                    entity.setFromAddress(transaction.getFrom());
                    entity.setToAddress(transaction.getTo());
                    entity.setValue(transaction.getValue());
                    entity.setBlockNumber(transaction.getBlockNumber());
                    return entity;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionEntity> getTransactions(String prefix) {
        return transactionRepository.findByTransactionHashStartingWith(prefix);
    }
}
