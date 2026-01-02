package ma.project.grpc.repositories;

import ma.project.grpc.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for BankAccountEntity entity
 * Provides data access methods for bank account operations
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, String> {

    /**
     * Calculates the total sum of all account balances
     * Uses COALESCE to return 0 if no accounts exist
     * @return The sum of all balances, or 0 if database is empty
     */
    @Query("SELECT COALESCE(SUM(c.solde), 0) FROM BankAccountEntity c")
    float getSumSolde();
}