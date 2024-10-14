package com.example.quips.analytics.service;

import com.example.quips.transaction.domain.model.Transaction;
import com.example.quips.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionActivityService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Map<String, Long> getTransactionActivityByHour() {
        List<Transaction> transactions = transactionRepository.findAll();
        Map<String, Long> activityByHour = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTimestamp().getHour() + ":00",
                        Collectors.counting()
                ));

        return activityByHour;
    }
}
