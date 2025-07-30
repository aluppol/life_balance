package com.luppol.life_balance.controllers;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PersonControllerIT extends AbstractControllerIT{
    @Test
    void create_successful() throws Exception {
        mvc.perform(post(PersonController.BASE_PATH)
                .contentType("application/json")
                .content("""
                        {"firstName": "John", "lastName": "Snow", "email": "john.snow@gmail.com"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Snow"))
                .andExpect(jsonPath("$.email").value("john.snow@gmail.com"))
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
                {"firstName": "John", "lastName": "Doe", "email": "john.doe@gmail.com"}
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
                {"firstName": "Jane", "lastName": "Smith", "email": "jane.smith@gmail.com"}
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
                {"firstName": "Old", "lastName": "Name", "email": "old.name@gmail.com"}
            """)).andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(response, "$.id")).longValue();

        mvc.perform(put(PersonController.BASE_PATH + "/{id}", id)
            .contentType("application/json")
            .content("""
                {"firstName": "New", "lastName": "Name", "email": "new.name@gmail.com"}
            """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("New"));
    }

    @Test
    void patch_updatesPersonFields() throws Exception {
        String response = mvc.perform(post(PersonController.BASE_PATH)
            .contentType("application/json")
            .content("""
                {"firstName": "Initial", "lastName": "Value", "email": "initial.value@gmail.com"}
            """)).andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(response, "$.id")).longValue();

        mvc.perform(patch(PersonController.BASE_PATH + "/{id}", id)
            .contentType("application/json")
            .content("""
                {"firstName": "Patched", "email": "patched.value@gmail.com"}
            """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Patched"))
            .andExpect(jsonPath("$.lastName").value("Value"))
            .andExpect(jsonPath("$.email").value("patched.value@gmail.com"));
    }

    @Test
    void patch_invalidInput_returnsBadRequest() throws Exception {
        String response = mvc.perform(post(PersonController.BASE_PATH)
            .contentType("application/json")
            .content("""
                {"firstName": "A", "lastName": "B", "email": "a.b@gmail.com"}
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
                {"firstName": "Del", "lastName": "Eted", "email": "gg@gmail.com"}
            """)).andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(response, "$.id")).longValue();

        mvc.perform(delete(PersonController.BASE_PATH + "/{id}", id))
            .andExpect(status().isNoContent());

        mvc.perform(get(PersonController.BASE_PATH + "/{id}", id))
            .andExpect(status().is4xxClientError());
    }
}
