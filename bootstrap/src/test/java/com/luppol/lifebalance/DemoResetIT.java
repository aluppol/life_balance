package com.luppol.lifebalance;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DemoResetIT extends IntegrationTest {
    private final RequestPostProcessor guest = person("guest-" + UUID.randomUUID(), "guest");

    @Test
    void demoResetCommand_restoresTheGuestWorkspaceAndExits() throws Exception {
        mvc.perform(put("/api/mission").with(guest).contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"Defaced\"}"))
                .andExpect(status().isOk());

        LifeBalanceApplication.main(new String[]{
                DemoReset.COMMAND,
                "--spring.datasource.url=" + POSTGRES.getJdbcUrl(),
                "--spring.datasource.username=" + POSTGRES.getUsername(),
                "--spring.datasource.password=" + POSTGRES.getPassword()});

        mvc.perform(get("/api/mission").with(guest)).andExpect(jsonPath("$.text", startsWith("I live by principles")));
    }

    @Test
    void demoReset_isRequestedOnlyByItsCommand() {
        assertThat(DemoReset.isRequested(new String[]{"demo-reset"})).isTrue();
        assertThat(DemoReset.isRequested(new String[]{"--server.port=8080"})).isFalse();
        assertThat(DemoReset.isRequested(new String[]{})).isFalse();
    }
}
