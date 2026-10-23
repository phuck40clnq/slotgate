package com.example.user.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.example.user.application.port.out.UserRepositoryPort;
import com.example.user.common.error.UserInfrasErrorCode;
import com.example.user.common.exception.InfrastructureException;
import com.example.user.domain.model.UserProfile;

/**
 * Adapter that implements {@link UserRepositoryPort} using Spring Data JPA.
 * <p>
 * This class acts as a bridge between the domain layer and the persistence layer,
 * converting domain {@link UserProfile} objects to {@link JpaUserEntity} and vice versa.
 * It delegates actual database operations to {@link JpaUserRepository}.
 * </p>
 */
@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final JpaUserRepository repository;

    /**
     * Constructs a new {@code UserPersistenceAdapter} with the given repository.
     *
     * @param repository the Spring Data JPA repository for user entities
     */
    public UserPersistenceAdapter(JpaUserRepository repository) {
        this.repository = repository;
    }

    /**
     * Saves a domain {@link UserProfile} to the database.
     * <p>
     * Converts the domain user to a {@link JpaUserEntity} and delegates the save
     * operation to {@link JpaUserRepository}.
     * </p>
     *
     * @param user the domain user to save
     * 
     * @throws InfrastructureException with code {@link UserInfrasErrorCode#DATABASE_CONSTRAINT_VIOLATION}
     * if database has a violation on constraint
     * 
     * @throws InfrastructureException with code {@link UserInfrasErrorCode#DATABASE_UNAVAILABLE}
     * if database is temporary unavailable
     */
    @Override
    public void save(UserProfile user) {
        try {
            JpaUserEntity entity = new JpaUserEntity(user);
            repository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new InfrastructureException(
                UserInfrasErrorCode.DATABASE_CONSTRAINT_VIOLATION,
                ex
            );
        } catch (DataAccessException ex) {
            throw new InfrastructureException(
                UserInfrasErrorCode.DATABASE_UNAVAILABLE,
                ex
            );
        }
    }

    /**
     * Finds a domain {@link UserProfile} by id.
     * <p>
     * Queries the database using {@link JpaUserRepository#findById(UUID)} and
     * converts the result to a domain {@link UserProfile} if present.
     * </p>
     *
     * @param userId the id of the user to find
     * @return an {@link Optional} containing the domain user if found, otherwise empty
     * @throws InfrastructureException with code {@link UserInfrasErrorCode#DATABASE_UNAVAILABLE}
     * if database is temporary unavailable
     */
    @Override
    public Optional<UserProfile> findById(UUID userId) {
        try {
            return repository.findById(userId).map(JpaUserEntity::toDomain);
        } catch (DataAccessException ex) {
            throw new InfrastructureException(
                UserInfrasErrorCode.DATABASE_UNAVAILABLE,
                ex
            );
        }
    }

    /**
     * Finds a domain {@link UserProfile} by email.
     * <p>
     * Queries the database using {@link JpaUserRepository#findByEmail(String)} and
     * converts the result to a domain {@link UserProfile} if present.
     * </p>
     *
     * @param email the email of the user to find
     * @return an {@link Optional} containing the domain user if found, otherwise empty
     * @throws InfrastructureException with code {@link UserInfrasErrorCode#DATABASE_UNAVAILABLE}
     * if database is temporary unavailable
     */
    @Override
    public Optional<UserProfile> findByEmail(String email) {
        try {
            return repository.findByEmail(email).map(JpaUserEntity::toDomain);
        } catch (DataAccessException ex) {
            throw new InfrastructureException(
                UserInfrasErrorCode.DATABASE_UNAVAILABLE,
                ex
            );
        }
    }
}
