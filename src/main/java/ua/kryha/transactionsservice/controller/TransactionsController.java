package ua.kryha.transactionsservice.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ua.kryha.transactionsservice.entity.TransactionEntity;
import ua.kryha.transactionsservice.service.TransactionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionsController {

    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionEntity> searchTransactions(
            @RequestParam("prefix") String prefix) {
        return transactionService.getTransactions(prefix);
    }
}
