package ma.project.grpc.config;

import ma.project.grpc.entities.BankAccountEntity;
import ma.project.grpc.repositories.BankAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Initializes the database with sample bank accounts on application startup
 * This component runs automatically when the application starts
 */
@Component
public class BankDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BankDataInitializer.class);
    private final BankAccountRepository compteRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BankDataInitializer(BankAccountRepository compteRepository) {
        this.compteRepository = compteRepository;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting database initialization with sample data...");

        // Clear existing data to avoid duplicates
        compteRepository.deleteAll();

        // Create sample accounts with varied data
        List<BankAccountEntity> sampleAccounts = Arrays.asList(
                createAccount(2500.75f, "2024-01-15", "COURANT"),
                createAccount(8900.00f, "2024-02-20", "EPARGNE"),
                createAccount(1250.50f, "2024-03-10", "COURANT"),
                createAccount(15000.00f, "2024-04-05", "EPARGNE"),
                createAccount(3750.25f, "2024-05-18", "COURANT"),
                createAccount(6200.80f, "2024-06-22", "EPARGNE"),
                createAccount(950.00f, "2024-07-08", "COURANT"),
                createAccount(22000.00f, "2024-08-14", "EPARGNE")
        );

        // Save all accounts to database
        compteRepository.saveAll(sampleAccounts);

        logger.info("Database initialization completed successfully");
        logger.info("Total accounts created: {}", sampleAccounts.size());
        
        // Log summary statistics
        float totalBalance = sampleAccounts.stream()
                .map(BankAccountEntity::getSolde)
                .reduce(0f, Float::sum);
        logger.info("Total balance across all accounts: {}", totalBalance);
    }

    /**
     * Helper method to create a new account
     */
    private BankAccountEntity createAccount(float balance, String creationDate, String accountType) {
        BankAccountEntity account = new BankAccountEntity();
        account.setSolde(balance);
        account.setDateCreation(creationDate);
        account.setType(accountType);
        return account;
    }
}
