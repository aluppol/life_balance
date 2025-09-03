package com.luppol.life_balance.auth;

import com.luppol.life_balance.auth.models.User;
import com.luppol.life_balance.auth.services.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DataJpaTest
public class UserRepositoryIT {
    @Container
    static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("db/create-schema.sql");

    @DynamicPropertySource
    static void cfg(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", db::getJdbcUrl);
        registry.add("spring.datasource.username", db::getUsername);
        registry.add("spring.datasource.password", db::getPassword);
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    @Test
    void save_and_query_by_username_and_email() {
        User saved = userRepository.save(User.builder()
                .username("test_user")
                .email("test@test.test")
                .password("hash_1")
                .build());

        assertTrue(userRepository.existsByUsername("test_user"));
        assertTrue(userRepository.existsByEmail("test@test.test"));

        Optional<User> byUsername = userRepository.findByUsername("test_user");
        assertTrue(byUsername.isPresent());
        assertEquals(saved.getId(), byUsername.get().getId());

        Optional<User> byEmail = userRepository.findByEmail("test@test.test");
        assertTrue(byEmail.isPresent());
        assertEquals(saved.getId(), byEmail.get().getId());
    }

    @Test
    void updatePassword_via_dml() {
        User u = userRepository.save(User.builder()
                .username("alice")
                .email("alice@example.com")
                .password("old_hash")
                .build());

        int rows = userRepository.updatePassword(u.getId(), "new_hash");
        assertEquals(1, rows);

        // ensure we read DB state, not cached entity
        em.clear();

        User reloaded = userRepository.findByUsername("alice").orElseThrow();
        assertEquals("new_hash", reloaded.getPassword());
    }

    @Test
    void updateEmail_via_dml() {
        User u = userRepository.save(User.builder()
                .username("bob")
                .email("bob@old.com")
                .password("hash")
                .build());

        int rows = userRepository.updateEmail(u.getId(), "bob@new.com");
        assertEquals(1, rows);

        em.clear();

        User reloaded = userRepository.findByUsername("bob").orElseThrow();
        assertEquals("bob@new.com", reloaded.getEmail());
    }

    /**
     * Optional: this test enforces the policy that save() cannot update credentials
     * when the entity fields are annotated with updatable=false.
     * If you haven't set updatable=false, this test will fail (by design).
     */
    @Test
    void save_does_not_update_credentials_when_updatable_false() {
        User u = userRepository.save(User.builder()
                .username("carol")
                .email("carol@a.com")
                .password("hash_a")
                .build());

        // try to change creds via entity + save()
        u.setEmail("carol@b.com");
        u.setPassword("hash_b");
        userRepository.save(u); // should NOT affect DB if columns are updatable=false
        em.flush();
        em.clear();

        User reloaded = userRepository.findByUsername("carol").orElseThrow();
        assertEquals("carol@a.com", reloaded.getEmail());
        assertEquals("hash_a", reloaded.getPassword());
    }
}
