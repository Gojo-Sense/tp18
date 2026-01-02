package ma.project.grpc;

import ma.project.grpc.entities.Compte;
import ma.project.grpc.repositories.CompteRepository;
import ma.project.grpc.services.CompteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class Tp17ApplicationTests {

    @Autowired
    private CompteService compteService;

    @Autowired
    private CompteRepository compteRepository;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeEach
    void setUp() {
        // Clean database before each test
        compteRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        // Verify Spring context loads successfully
        assertThat(compteService).isNotNull();
        assertThat(compteRepository).isNotNull();
    }

    @Test
    void testSaveAndRetrieveAccount() {
        // Create and save a new account
        Compte newAccount = new Compte();
        newAccount.setSolde(1500.0f);
        newAccount.setDateCreation(LocalDate.now().format(dateFormatter));
        newAccount.setType("COURANT");

        Compte savedAccount = compteService.saveCompte(newAccount);

        // Verify account was saved with ID
        assertThat(savedAccount.getId()).isNotNull();
        assertThat(savedAccount.getSolde()).isEqualTo(1500.0f);
        assertThat(savedAccount.getType()).isEqualTo("COURANT");
    }

    @Test
    void testFindAllAccounts() {
        // Create multiple test accounts
        createTestAccount(2000.0f, "EPARGNE");
        createTestAccount(3500.0f, "COURANT");
        createTestAccount(1200.0f, "EPARGNE");

        // Retrieve all accounts
        List<Compte> allAccounts = compteService.findAllComptes();

        // Verify count
        assertThat(allAccounts).hasSize(3);
    }

    @Test
    void testFindAccountById() {
        // Create and save account
        Compte account = createTestAccount(5000.0f, "COURANT");

        // Retrieve by ID
        Compte foundAccount = compteService.findCompteById(account.getId());

        // Verify retrieval
        assertThat(foundAccount).isNotNull();
        assertThat(foundAccount.getId()).isEqualTo(account.getId());
        assertThat(foundAccount.getSolde()).isEqualTo(5000.0f);
    }

    @Test
    void testBalanceStatistics() {
        // Create accounts with known balances
        createTestAccount(1000.0f, "COURANT");
        createTestAccount(2000.0f, "EPARGNE");
        createTestAccount(3000.0f, "COURANT");

        // Get statistics
        var stats = compteService.getSoldeStats();

        // Verify calculations
        assertThat(stats.getCount()).isEqualTo(3);
        assertThat(stats.getSum()).isEqualTo(6000.0f);
        assertThat(stats.getAverage()).isEqualTo(2000.0f);
    }

    @Test
    void testEmptyDatabaseStatistics() {
        // Get statistics on empty database
        var stats = compteService.getSoldeStats();

        // Verify zero values
        assertThat(stats.getCount()).isZero();
        assertThat(stats.getSum()).isZero();
        assertThat(stats.getAverage()).isZero();
    }

    @Test
    void testMultipleAccountTypes() {
        // Create different account types
        createTestAccount(1500.0f, "COURANT");
        createTestAccount(2500.0f, "EPARGNE");

        List<Compte> accounts = compteService.findAllComptes();

        // Verify both types exist
        long courantCount = accounts.stream()
                .filter(c -> c.getType().equals("COURANT"))
                .count();
        long epargneCount = accounts.stream()
                .filter(c -> c.getType().equals("EPARGNE"))
                .count();

        assertThat(courantCount).isEqualTo(1);
        assertThat(epargneCount).isEqualTo(1);
    }

    /**
     * Helper method to create test accounts
     */
    private Compte createTestAccount(float balance, String type) {
        Compte account = new Compte();
        account.setSolde(balance);
        account.setDateCreation(LocalDate.now().format(dateFormatter));
        account.setType(type);
        return compteService.saveCompte(account);
    }
}
