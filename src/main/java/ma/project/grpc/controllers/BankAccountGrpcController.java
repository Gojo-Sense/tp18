package ma.project.grpc.controllers;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ma.project.grpc.services.BankAccountService;
import ma.project.grpc.stubs.*;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * gRPC Service implementation for bank account management
 * Handles account creation, retrieval, and statistics calculation
 */
@GrpcService
public class BankAccountGrpcController extends CompteServiceGrpc.CompteServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountGrpcController.class);
    private final BankAccountService compteService;

    public BankAccountGrpcController(BankAccountService compteService) {
        this.compteService = compteService;
        logger.info("BankAccountGrpcController initialized successfully");
    }

    /**
     * Converts a JPA entity to a gRPC message
     * @param entity The Compte entity from database
     * @return The gRPC Compte message
     */
    private Compte mapEntityToProto(ma.project.grpc.entities.BankAccountEntity entity) {
        return Compte.newBuilder()
                .setId(entity.getId())
                .setSolde(entity.getSolde())
                .setDateCreation(entity.getDateCreation())
                .setType(TypeCompte.valueOf(entity.getType()))
                .build();
    }

    @Override
    public void allComptes(GetAllComptesRequest request, StreamObserver<GetAllComptesResponse> responseObserver) {
        try {
            logger.debug("Fetching all bank accounts");
            
            List<Compte> grpcComptes = compteService.findAllBankAccounts()
                    .stream()
                    .map(this::mapEntityToProto)
                    .collect(Collectors.toList());

            GetAllComptesResponse response = GetAllComptesResponse.newBuilder()
                    .addAllComptes(grpcComptes)
                    .build();

            logger.info("Successfully retrieved {} accounts", grpcComptes.size());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("Error retrieving all accounts", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to retrieve accounts: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void compteById(GetCompteByIdRequest request, StreamObserver<GetCompteByIdResponse> responseObserver) {
        try {
            String accountId = request.getId();
            logger.debug("Searching for account with ID: {}", accountId);

            ma.project.grpc.entities.BankAccountEntity entity = compteService.findBankAccountById(accountId);

            if (entity == null) {
                logger.warn("Account not found with ID: {}", accountId);
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Account with ID '" + accountId + "' does not exist")
                        .asRuntimeException());
                return;
            }

            Compte grpcCompte = mapEntityToProto(entity);
            GetCompteByIdResponse response = GetCompteByIdResponse.newBuilder()
                    .setCompte(grpcCompte)
                    .build();

            logger.info("Account found successfully: {}", accountId);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("Error retrieving account by ID", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void totalSolde(GetTotalSoldeRequest request, StreamObserver<GetTotalSoldeResponse> responseObserver) {
        try {
            logger.debug("Calculating balance statistics");
            
            SoldeStats statistics = compteService.getSoldeStats();

            GetTotalSoldeResponse response = GetTotalSoldeResponse.newBuilder()
                    .setStats(statistics)
                    .build();

            logger.info("Statistics calculated - Accounts: {}, Total: {}, Average: {}", 
                    statistics.getCount(), statistics.getSum(), statistics.getAverage());
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("Error calculating balance statistics", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to calculate statistics: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void saveCompte(SaveCompteRequest request, StreamObserver<SaveCompteResponse> responseObserver) {
        try {
            CompteRequest requestData = request.getCompte();
            logger.debug("Saving new account - Type: {}, Balance: {}", 
                    requestData.getType(), requestData.getSolde());

            // Validate balance
            if (requestData.getSolde() < 0) {
                logger.warn("Attempt to create account with negative balance");
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Balance cannot be negative")
                        .asRuntimeException());
                return;
            }

            // Create entity from request
            ma.project.grpc.entities.BankAccountEntity newAccount = new ma.project.grpc.entities.BankAccountEntity();
            newAccount.setSolde(requestData.getSolde());
            newAccount.setDateCreation(requestData.getDateCreation());
            newAccount.setType(requestData.getType().name());

            // Save to database
            ma.project.grpc.entities.BankAccountEntity savedAccount = compteService.saveBankAccount(newAccount);

            // Convert to gRPC response
            Compte grpcCompte = mapEntityToProto(savedAccount);
            SaveCompteResponse response = SaveCompteResponse.newBuilder()
                    .setCompte(grpcCompte)
                    .build();

            logger.info("Account saved successfully with ID: {}", savedAccount.getId());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("Error saving account", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to save account: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}