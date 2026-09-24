package com.luppol.lifebalance.adapter.web.spa;

import com.luppol.lifebalance.adapter.web.WebTest;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SinglePageApplicationTest extends WebTest {
    @Test
    void clientRoute_servesTheApplicationShell() throws Exception {
        mvc.perform(get("/week/2026-09-21")).andExpect(status().isOk())
                .andExpect(content().string(containsString("<div id=\"root\"></div>")));
    }

    @Test
    void existingFile_isServedAsIs() throws Exception {
        mvc.perform(get("/index.html")).andExpect(status().isOk());
    }

    @Test
    void missingAsset_isNotFound() throws Exception {
        mvc.perform(get("/assets/missing.js")).andExpect(status().isNotFound());
    }

    @Test
    void unknownApiPath_isNotTheShell() throws Exception {
        mvc.perform(get("/api/unknown").with(member())).andExpect(status().isNotFound());
    }

    @Test
    void actuatorPath_isNotTheShell() throws Exception {
        mvc.perform(get("/actuator/unknown")).andExpect(status().isNotFound());
    }

    @Test
    void missingShell_isNotFound() throws Exception {
        SinglePageApplicationResolverProbe probe = new SinglePageApplicationResolverProbe();
        assertThat(probe.resolve("week/2026-09-21", new ClassPathResource("missing/"))).isNull();
    }
}

