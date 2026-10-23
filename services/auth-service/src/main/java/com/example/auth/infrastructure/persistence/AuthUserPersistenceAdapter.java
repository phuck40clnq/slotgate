package com.example.auth.infrastructure.persistence;

import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.example.auth.application.port.out.AuthUserRepositoryPort;
import com.example.auth.common.error.AuthInfrasErrorCode;
import com.example.auth.common.exception.InfrastructureException;
import com.example.auth.domain.model.AuthUser;

@Component
public class AuthUserPersistenceAdapter implements AuthUserRepositoryPort {
    
    private final JpaAuthUserRepository repository;

    public AuthUserPersistenceAdapter(JpaAuthUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(AuthUser user) {
        try {
            JpaAuthUserEntity entity = new JpaAuthUserEntity(user);
            repository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new InfrastructureException(
                AuthInfrasErrorCode.DATABASE_CONSTRAINT_VIOLATION,
                ex
            );
        } catch (DataAccessException ex) {
            throw new InfrastructureException(
                AuthInfrasErrorCode.DATABASE_UNAVAILABLE,
                ex
            );
        }
    }

    @Override
    public Optional<AuthUser> findByEmail(String email) {
        try {
            return repository.findByEmail(email).map(JpaAuthUserEntity::toDomain);
        } catch (DataAccessException ex) {
            throw new InfrastructureException(
                AuthInfrasErrorCode.DATABASE_UNAVAILABLE,
                ex
            );
        }
    }

}
