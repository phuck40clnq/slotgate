package com.example.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAuthUserRepository extends JpaRepository<JpaAuthUserEntity, UUID> {
    Optional<JpaAuthUserEntity> findByEmail(String email);
}
