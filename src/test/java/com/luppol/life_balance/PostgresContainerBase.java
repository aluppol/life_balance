package com.luppol.life_balance;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class PostgresContainerBase {
    @Container
    static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:16-alpine") // with @Container on static final method there is no resources leaks.
            .withInitScript("db/create-schema.sql");

    @DynamicPropertySource
    static void cfg(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", db::getJdbcUrl);
        registry.add("spring.datasource.username", db::getUsername);
        registry.add("spring.datasource.password", db::getPassword);
    }
}
