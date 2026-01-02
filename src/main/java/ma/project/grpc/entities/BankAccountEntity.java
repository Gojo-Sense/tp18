package ma.project.grpc.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * JPA Entity representing a bank account
 * Stores account information including balance, creation date, and type
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountEntity {
    
    /**
     * Unique identifier for the account (UUID format)
     */
    @Id
    private String id;
    
    /**
     * Current balance of the account
     */
    private float solde;
    
    /**
     * Date when the account was created (format: yyyy-MM-dd)
     */
    private String dateCreation;
    
    /**
     * Type of account (COURANT or EPARGNE)
     */
    private String type;

    /**
     * Automatically generates a unique ID before persisting to database
     * Only generates if ID is not already set
     */
    @PrePersist
    public void generateId() {
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }
    }
}