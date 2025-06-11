package com.luppol.life_balance.models;

import com.luppol.life_balance.repositories.MissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
public class MissionRepositoryIT {
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
    MissionRepository repo;

    @Test
    void safeAndFind() {
        Mission saved = repo.save(Mission.builder().text("To be happy!").build());
        assertTrue(repo.findById(saved.getId()).isPresent());
    }

}
