package com.example.quips.service;

import com.example.quips.model.DAG;
import com.example.quips.model.Transaction;
import com.example.quips.model.Wallet;
import com.example.quips.repository.TransactionRepository;
import com.example.quips.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final DAG dag;
    private final SistemaService sistemaService;
    private final WalletService walletService;  // Añadir WalletService

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository, DAG dag, SistemaService sistemaService, WalletService walletService) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.dag = dag;
        this.sistemaService = sistemaService;
        this.walletService = walletService;  // Inyectar WalletService

        // Inicializar el DAG con transacciones existentes
        initializeDAG();
    }

    @Transactional
    public Transaction createTransaction(Long senderWalletId, Long receiverWalletId, double amount) {
        // Obtener las wallets involucradas en la transacción
        Wallet senderWallet = walletRepository.findById(senderWalletId)
                .orElseThrow(() -> new IllegalArgumentException("Sender wallet ID not found"));
        Wallet receiverWallet = walletRepository.findById(receiverWalletId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver wallet ID not found"));

        // Verificar que la wallet del remitente tenga fondos suficientes
        if (senderWallet.getCoins() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        // Buscar los hashes de las dos transacciones anteriores más recientes
        String[] previousTransactionHashes = findPreviousTransactionHashes();

        // Validar que los hashes no sean nulos o estén vacíos
        if (previousTransactionHashes == null || previousTransactionHashes.length != 2
                || previousTransactionHashes[0] == null || previousTransactionHashes[1] == null) {
            throw new IllegalStateException("Error retrieving previous transaction hashes");
        }

        // Obtener la fase actual
        int faseActual = sistemaService.getFaseActual();

        // Crear la nueva transacción
        Transaction transaction = new Transaction(senderWalletId, receiverWalletId, amount,
                previousTransactionHashes[0], previousTransactionHashes[1], faseActual);

        // Agregar la transacción al DAG antes de validarla
        dag.addTransaction(transaction);

        // Validar la transacción
        if (!dag.validateTransaction(transaction)) {
            throw new IllegalArgumentException("Transaction validation failed");
        }

        // Realizar la transferencia de monedas utilizando WalletService
        walletService.subtractCoins(senderWallet, amount);
        walletService.addCoins(receiverWallet, amount);

        // Guardar la transacción en el repositorio
        transactionRepository.save(transaction);

        // Registrar la transacción en el sistema
        sistemaService.registrarTransaccion();



        return transaction;
    }

    private String[] findPreviousTransactionHashes() {
        // Obtener las dos transacciones más recientes
        List<Transaction> previousTransactions = transactionRepository.findTop2ByOrderByIdDesc();

        // Definir los hashes de génesis por defecto
        String hash1 = "genesis_hash1";
        String hash2 = "genesis_hash2";

        // Si hay transacciones previas, actualizar los hashes
        if (!previousTransactions.isEmpty()) {
            hash1 = previousTransactions.get(0).getHash();
            if (previousTransactions.size() > 1) {
                hash2 = previousTransactions.get(1).getHash();
            }
        }

        // Retornar siempre dos hashes válidos
        return new String[]{hash1, hash2};
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public boolean deleteTransaction(Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Inicializa el DAG con todas las transacciones existentes en la base de datos
    private void initializeDAG() {
        List<Transaction> transactions = transactionRepository.findAll();
        transactions.forEach(dag::addTransaction);
        System.out.println("DAG inicializado con " + transactions.size() + " transacciones.");
    }
}
