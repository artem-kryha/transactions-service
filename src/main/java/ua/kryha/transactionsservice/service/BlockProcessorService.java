package ua.kryha.transactionsservice.service;

import io.reactivex.disposables.Disposable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.websocket.WebSocketService;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;

@Slf4j
@Service
public class BlockProcessorService {

    private static final int ONE = 1;
    private long lastProcessedBlock;
    private boolean isProcessingGaps = false;

    private final TransactionService transactionService;

    private Web3j web3j;
    private Disposable blockSubscription;

    @Value("${evm.url}")
    private String evmUrl;

    @Autowired
    public BlockProcessorService(TransactionService transactionService, BlockTrackerService blockTrackerService) {
        this.transactionService = transactionService;
        this.lastProcessedBlock = blockTrackerService.getLastProcessedBlock();
    }

    @PostConstruct
    public void setUp() {
        try {
            WebSocketService webSocketService = new WebSocketService(evmUrl, true);
            webSocketService.connect();
            this.web3j = Web3j.build(webSocketService);

            startBlockSubscription();
        } catch (ConnectException e) {
            log.error("Failed to connect to WebSocket service: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void stopBlockSubscription() {
        if (blockSubscription != null && !blockSubscription.isDisposed()) {
            blockSubscription.dispose();
        }
    }

    public void startBlockSubscription() {
        blockSubscription = web3j.blockFlowable(true).subscribe(block -> {
            long currentBlockNumber = block.getBlock().getNumber().longValue();

            if (shouldProcessGaps(currentBlockNumber)) {
                processGapsSequentially(lastProcessedBlock + ONE, currentBlockNumber - ONE);
            }
            if (isSequential(currentBlockNumber)) {
                processBlockDirectly(block.getBlock());
            }
        }, error -> log.warn("Failed to process block with number: {}", lastProcessedBlock));
    }

    public void processGapsSequentially(long startBlock, long endBlock) throws IOException {
        for (long blockNumber = startBlock; blockNumber <= endBlock; blockNumber++) {
            EthBlock.Block block = fetchAndProcess(blockNumber);
            log.info("Processed gap block {}", block.getNumber().toString());
        }
        isProcessingGaps = false;
    }

    private EthBlock.Block fetchAndProcess(long blockNumber) throws IOException {
        EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)), true).send();
        EthBlock.Block block = ethBlock.getBlock();
        transactionService.processBlock(block);
        lastProcessedBlock = block.getNumber().longValue();
        return block;
    }

    public void processBlockDirectly(EthBlock.Block block) {
        if (block.getNumber().longValue() > lastProcessedBlock) {
            transactionService.processBlock(block);
            lastProcessedBlock = block.getNumber().longValue();
            log.info("Processed DIRECTLY {}", block.getNumber().toString());
        }
    }

    private boolean isSequential(long currentBlockNumber) {
        return !isProcessingGaps && currentBlockNumber == lastProcessedBlock + ONE;
    }

    private boolean shouldProcessGaps(long currentBlockNumber) {
        return currentBlockNumber > lastProcessedBlock + ONE && !isProcessingGaps;
    }
}