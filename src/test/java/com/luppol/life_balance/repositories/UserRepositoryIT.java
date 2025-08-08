package com.luppol.life_balance.repositories;

import com.luppol.life_balance.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

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

    @Test
    void saveAndQueryByUsername() {
        final String EMAIL = "test@test.test";
        final String USERNAME = "test_user";
        final String PASSWORD = "test_pass_hash";

        User savedUser = userRepository.save(User.builder()
                .email(EMAIL)
                .username(USERNAME)
                .password(PASSWORD)
                .build()
        );

        assertTrue(userRepository.existsByUsername(USERNAME));
        assertTrue(userRepository.findById(savedUser.getId()).isPresent());
        assertEquals(USERNAME, savedUser.getUsername());
        assertEquals(PASSWORD, savedUser.getPassword());
        assertEquals(EMAIL, savedUser.getEmail());
    }
}
