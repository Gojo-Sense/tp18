package ma.project.grpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for gRPC Bank Account Service
 * 
 * This application implements a gRPC service for managing bank accounts
 * Features include:
 * - Account creation and retrieval
 * - Balance statistics calculation
 * - H2 in-memory database for data persistence
 * - Automatic data initialization on startup
 * 
 * gRPC server runs on port 9090 (configured in application.properties)
 */
@SpringBootApplication
public class BankAccountGrpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankAccountGrpcApplication.class, args);
    }
}
