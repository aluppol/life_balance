package com.luppol.life_balance.controllers;

import com.jayway.jsonpath.JsonPath;
import com.luppol.life_balance.mappers.PersonMapper;
import com.luppol.life_balance.models.Person;
import com.luppol.life_balance.repositories.PersonRepository;
import com.luppol.life_balance.services.PersonService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PersonControllerIT {

    @Autowired
    MockMvc mvc;

    @Container
    static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("db/create-schema.sql");

    @DynamicPropertySource
    static void cfg(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", db::getJdbcUrl);
        registry.add("spring.datasource.username", db::getUsername);
        registry.add("spring.datasource.password", db::getPassword);
    }

    @Test
    void create_successful() throws Exception {
        mvc.perform(post(PersonController.BASE_PATH)
                .contentType("application/json")
                .content("""
                        {"firstName": "John", "lastName": "Snow"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Snow"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void create_invalidInput_returnsBadRequest() throws Exception {
        mvc.perform(post(PersonController.BASE_PATH)
            .contentType("application/json")
            .content("""
                {"firstName": ""}
            """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_returnsListOfPersons() throws Exception {
        mvc.perform(post(PersonController.BASE_PATH)
            .contentType("application/json")
            .content("""
                {"firstName": "John", "lastName": "Doe"}
            """))
            .andExpect(status()
            .isCreated());

        mvc.perform(get(PersonController.BASE_PATH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void getById_returnsPerson() throws Exception {
        String response = mvc.perform(post(PersonController.BASE_PATH)
            .contentType("application/json")
            .content("""
                {"firstName": "Jane", "lastName": "Smith"}
            """)).andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(response, "$.id")).longValue();

        mvc.perform(get(PersonController.BASE_PATH + "/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void getById_notFound_returnsError() throws Exception {
        mvc.perform(get(PersonController.BASE_PATH + "/{id}", 99999))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void put_updatesPerson() throws Exception {
        String response = mvc.perform(post(PersonController.BASE_PATH)
            .contentType("application/json")
            .content("""
                {"firstName": "Old", "lastName": "Name"}
            """)).andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(response, "$.id")).longValue();

        mvc.perform(put(PersonController.BASE_PATH + "/{id}", id)
            .contentType("application/json")
            .content("""
                {"firstName": "New", "lastName": "Name"}
            """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("New"));
    }

    @Test
    void patch_updatesPersonFields() throws Exception {
        String response = mvc.perform(post(PersonController.BASE_PATH)
            .contentType("application/json")
            .content("""
                {"firstName": "Initial", "lastName": "Value"}
            """)).andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(response, "$.id")).longValue();

        mvc.perform(patch(PersonController.BASE_PATH + "/{id}", id)
            .contentType("application/json")
            .content("""
                {"firstName": "Patched"}
            """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Patched"))
            .andExpect(jsonPath("$.lastName").value("Value"));
    }

    @Test
    void patch_invalidInput_returnsBadRequest() throws Exception {
        String response = mvc.perform(post(PersonController.BASE_PATH)
            .contentType("application/json")
            .content("""
                {"firstName": "A", "lastName": "B"}
            """)).andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(response, "$.id")).longValue();

        mvc.perform(patch(PersonController.BASE_PATH + "/{id}", id)
            .contentType("application/json")
            .content("""
                {"firstName": ""}
            """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void delete_removesPerson() throws Exception {
        String response = mvc.perform(post(PersonController.BASE_PATH)
            .contentType("application/json")
            .content("""
                {"firstName": "Del", "lastName": "Eted"}
            """)).andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(response, "$.id")).longValue();

        mvc.perform(delete(PersonController.BASE_PATH + "/{id}", id))
            .andExpect(status().isNoContent());

        mvc.perform(get(PersonController.BASE_PATH + "/{id}", id))
            .andExpect(status().is4xxClientError());
    }
}
