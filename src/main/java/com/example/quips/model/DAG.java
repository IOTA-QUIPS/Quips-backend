package com.example.quips.model;

import java.util.HashMap;
import java.util.Map;

public class DAG {
    private Map<String, Transaction> transactions = new HashMap<>();

    public void addTransaction(Transaction transaction) {
        transactions.put(transaction.getHash(), transaction);
    }

    public boolean validateTransaction(Transaction transaction) {
        if (transaction.getPreviousTransactionHash().equals("genesis_hash")) {
            return true; // La primera transacción no requiere validación
        }

        if (!transactions.containsKey(transaction.getPreviousTransactionHash())) {
            System.out.println("Error: Transacción previa con hash " + transaction.getPreviousTransactionHash() + " no encontrada.");
            return false;
        }

        String calculatedHash = transaction.calculateHash();
        if (!calculatedHash.equals(transaction.getHash())) {
            System.out.println("Error: Hash de la transacción no coincide. Hash esperado: " + transaction.getHash() + ", Hash calculado: " + calculatedHash);
            return false;
        }

        System.out.println("Transacción " + transaction.getId() + " validada con éxito.");
        return true;
    }

    public boolean containsTransaction(String hash) {
        return transactions.containsKey(hash);
    }
}
