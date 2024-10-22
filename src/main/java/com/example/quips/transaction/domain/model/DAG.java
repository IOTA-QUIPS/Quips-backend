package com.example.quips.transaction.domain.model;

import java.util.HashMap;
import java.util.Map;

public class DAG {
    private final Map<String, Transaction> transactions = new HashMap<>();

    public void addTransaction(Transaction transaction) {
        transactions.put(transaction.getHash(), transaction);
        System.out.println("Transacción añadida al DAG con hash: " + transaction.getHash());
    }

    // Método para obtener todas las transacciones
    public Map<String, Transaction> getTransactions() {
        return transactions;
    }

    public boolean validateTransaction(Transaction transaction) {
        System.out.println("Validando transacción: " + transaction.getId());

        // Verificar si los hashes anteriores son nulos
        if (transaction.getPreviousTransactionHash1() == null || transaction.getPreviousTransactionHash2() == null) {
            System.out.println("Error: Los hashes anteriores no pueden ser nulos.");
            return false;
        }

        // Verificar si la transacción usa los hashes génesis
        if (transaction.getPreviousTransactionHash1().equals("genesis_hash1") &&
                transaction.getPreviousTransactionHash2().equals("genesis_hash2")) {
            System.out.println("Transacción válida: utiliza transacciones génesis.");
            return true;
        }

        // Verificar la existencia de las transacciones previas en el DAG
        if (!transactions.containsKey(transaction.getPreviousTransactionHash1()) &&
                !transaction.getPreviousTransactionHash1().equals("genesis_hash1")) {
            System.out.println("Error: Transacción previa 1 con hash " + transaction.getPreviousTransactionHash1() + " no encontrada.");
            return false;
        }

        if (!transactions.containsKey(transaction.getPreviousTransactionHash2()) &&
                !transaction.getPreviousTransactionHash2().equals("genesis_hash2")) {
            System.out.println("Error: Transacción previa 2 con hash " + transaction.getPreviousTransactionHash2() + " no encontrada.");
            return false;
        }

        // Calcular el hash actual y compararlo con el hash esperado
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
