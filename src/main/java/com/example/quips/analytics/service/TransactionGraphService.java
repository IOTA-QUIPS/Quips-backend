package com.example.quips.analytics.service;

import com.example.quips.transaction.domain.model.DAG;
import com.example.quips.transaction.domain.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionGraphService {

    @Autowired
    private DAG dag;

    public Map<String, Object> getTransactionGraph() {
        Map<String, Transaction> transactionGraph = dag.getTransactions();

        Map<String, Object> graph = new HashMap<>();
        graph.put("totalTransactions", transactionGraph.size());
        graph.put("transactions", transactionGraph);
        return graph;
    }
}
