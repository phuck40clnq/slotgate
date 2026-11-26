package com.example.auth.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import com.example.auth.common.error.AuthInfrasErrorCode;
import com.example.auth.common.exception.InfrastructureException;
import com.example.auth.domain.model.AuthUser;
import com.example.auth.support.AuthUserFixtures;
import com.example.auth.support.PostgresIntegrationTest;

@RequiredArgsConstructor
class AuthUserPersistenceAdapterIT extends PostgresIntegrationTest {

    private final AuthUserPersistenceAdapter adapter;

    @Test
    void findsSavedUserByEmail() {
        AuthUser user = AuthUserFixtures.user("00000000-0000-0000-0000-0000000000a1", "found@example.test");
        adapter.save(user);

        Optional<AuthUser> found = adapter.findByEmail("found@example.test");

        assertThat(found).get()
            .returns(user.getId(), AuthUser::getId)
            .returns(user.getPasswordHash(), AuthUser::getPasswordHash);
    }

    @Test
    void findsNoUserForUnknownEmail() {
        Optional<AuthUser> found = adapter.findByEmail("unknown@example.test");

        assertThat(found).isEmpty();
    }

    @Test
    void rejectsSecondUserWithTheSameEmail() {
        adapter.save(AuthUserFixtures.user("00000000-0000-0000-0000-0000000000b1", "taken@example.test"));
        AuthUser sameEmail = AuthUserFixtures.user("00000000-0000-0000-0000-0000000000b2", "taken@example.test");

        assertThatThrownBy(() -> adapter.save(sameEmail))
            .isInstanceOfSatisfying(InfrastructureException.class, exception -> assertThat(exception.getErrorCode())
                .isEqualTo(AuthInfrasErrorCode.DATABASE_CONSTRAINT_VIOLATION));
    }
}
