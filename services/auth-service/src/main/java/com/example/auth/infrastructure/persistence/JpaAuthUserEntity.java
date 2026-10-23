package com.example.auth.infrastructure.persistence;

import java.util.UUID;

import com.example.auth.domain.model.AuthUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auth_users")
public class JpaAuthUserEntity {
    
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    protected JpaAuthUserEntity() {} // Hibernate

    public JpaAuthUserEntity(AuthUser user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
    }

    public AuthUser toDomain() {
        return new AuthUser(this.id, this.email, this.passwordHash);
    }

}
