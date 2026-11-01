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
    private final RequestPostProcessor guest = guestVisit("guest-" + UUID.randomUUID(), "session-" + UUID.randomUUID());

    @Test
    void demoResetCommand_restoresTheGuestWorkspaceAndExits() throws Exception {
        mvc.perform(put("/api/mission").with(guest).contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"Defaced\"}"))
                .andExpect(status().isOk());

        LifeBalanceApplication.main(operatorArguments(DemoReset.NAME));

        mvc.perform(get("/api/mission").with(guest)).andExpect(jsonPath("$.text", startsWith("I live by principles")));
    }

    @Test
    void operatorCommands_areRequestedOnlyByTheirNames() {
        assertThat(OperatorCommands.isRequested(new String[]{"demo-reset"})).isTrue();
        assertThat(OperatorCommands.isRequested(new String[]{"inactive-members", "365"})).isTrue();
        assertThat(OperatorCommands.isRequested(new String[]{"remove-inactive-members", "365"})).isTrue();
        assertThat(OperatorCommands.isRequested(new String[]{"--server.port=8080"})).isFalse();
        assertThat(OperatorCommands.isRequested(new String[]{"365", "inactive-members"})).isFalse();
        assertThat(OperatorCommands.isRequested(new String[]{})).isFalse();
    }
}
