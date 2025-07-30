package com.luppol.life_balance.controllers;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MissionControllerIT extends AbstractControllerIT {

    @Test
    void create_successful() throws Exception {
        mvc.perform(post(MissionController.BASE_PATH)
                        .contentType("application/json")
                        .content("""
                    {"name": "My Mission"}
                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("My Mission"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void create_invalidInput_returnsBadRequest() throws Exception {
        mvc.perform(post(MissionController.BASE_PATH)
                        .contentType("application/json")
                        .content("""
                    {"name": ""}
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void put_updatesMission() throws Exception {
        long id = createMission("Old Mission");

        mvc.perform(put(MissionController.BASE_PATH + "/{id}", id)
                        .contentType("application/json")
                        .content("""
                    {"name": "Updated Mission"}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Mission"));
    }

    @Test
    void patch_updatesMissionFields() throws Exception {
        long id = createMission("Initial Mission");

        mvc.perform(patch(MissionController.BASE_PATH + "/{id}", id)
                        .contentType("application/json")
                        .content("""
                    {"name": "Patched Mission"}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Patched Mission"));
    }

    @Test
    void patch_invalidInput_returnsBadRequest() throws Exception {
        long id = createMission("Valid Mission");

        mvc.perform(patch(MissionController.BASE_PATH + "/{id}", id)
                        .contentType("application/json")
                        .content("""
                    {"name": ""}
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_removesMission() throws Exception {
        long id = createMission("To Be Deleted");

        mvc.perform(delete(MissionController.BASE_PATH + "/{id}", id))
                .andExpect(status().isNoContent());

        mvc.perform(get(MissionController.BASE_PATH + "/{id}", id))
                .andExpect(status().isNotFound());
    }

    // Utility method for test readability
    private long createMission(String name) throws Exception {
        MvcResult result = mvc.perform(post(MissionController.BASE_PATH)
                        .contentType("application/json")
                        .content("{\"name\": \"%s\"}".formatted(name)))
                .andExpect(status().isCreated())
                .andReturn();

        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
    }
}
