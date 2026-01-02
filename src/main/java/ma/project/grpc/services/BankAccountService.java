package ma.project.grpc.services;

import ma.project.grpc.entities.BankAccountEntity;
import ma.project.grpc.repositories.CompteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for managing bank account operations
 * Provides business logic for account management and statistics
 */
@Service
@Transactional
public class BankAccountService {
    
    private static final Logger logger = LoggerFactory.getLogger(BankAccountService.class);
    private final BankAccountRepository compteRepository;

    public BankAccountService(BankAccountRepository compteRepository) {
        this.compteRepository = compteRepository;
        logger.info("BankAccountService initialized");
    }

    /**
     * Retrieves all accounts from the database
     * @return List of all bank accounts
     */
    public List<BankAccountEntity> findAllBankAccounts() {
        logger.debug("Fetching all accounts from database");
        return compteRepository.findAll();
    }

    /**
     * Finds a specific account by its unique identifier
     * @param id The account ID to search for
     * @return The account if found, null otherwise
     */
    public BankAccountEntity findBankAccountById(String id) {
        logger.debug("Searching for account with ID: {}", id);
        return compteRepository.findById(id).orElse(null);
    }

    /**
     * Saves a new account or updates an existing one
     * @param compte The account to save
     * @return The saved account with generated ID if new
     */
    public BankAccountEntity saveBankAccount(BankAccountEntity compte) {
        logger.debug("Saving account to database");
        BankAccountEntity savedCompte = compteRepository.save(compte);
        logger.info("Account saved successfully with ID: {}", savedCompte.getId());
        return savedCompte;
    }

    /**
     * Calculates comprehensive statistics for all account balances
     * Includes total count, sum, and average of all balances
     * @return SoldeStats object containing statistical information
     */
    public ma.project.grpc.stubs.SoldeStats getSoldeStats() {
        logger.debug("Calculating balance statistics");
        
        int totalAccounts = (int) compteRepository.count();
        float totalBalance = compteRepository.getSumSolde();
        float averageBalance = totalAccounts > 0 ? totalBalance / totalAccounts : 0;

        logger.info("Statistics - Total Accounts: {}, Total Balance: {}, Average: {}", 
                totalAccounts, totalBalance, averageBalance);

        return ma.project.grpc.stubs.SoldeStats.newBuilder()
                .setCount(totalAccounts)
                .setSum(totalBalance)
                .setAverage(averageBalance)
                .build();
    }
}