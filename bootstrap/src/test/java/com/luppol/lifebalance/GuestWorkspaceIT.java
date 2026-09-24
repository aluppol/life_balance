package com.luppol.lifebalance;

import com.luppol.lifebalance.application.person.PersonCommands;
import com.luppol.lifebalance.domain.planning.WeekStart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GuestWorkspaceIT extends IntegrationTest {
    private final RequestPostProcessor guest = person("guest-" + UUID.randomUUID(), "guest");
    private final RequestPostProcessor member = person("member-" + UUID.randomUUID(), "USER");

    @Autowired
    private PersonCommands personCommands;

    @Autowired
    private Clock clock;

    @Test
    void guest_findsAFurnishedPlanner() throws Exception {
        mvc.perform(get("/api/mission").with(guest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", startsWith("I live by principles")));
        mvc.perform(get("/api/roles").with(guest)).andExpect(jsonPath("$", hasSize(5)));
        mvc.perform(get("/api/goals").with(guest)).andExpect(jsonPath("$", hasSize(6)));
        mvc.perform(get("/api/weeks/{week}/activities", thisWeek()).with(guest)).andExpect(jsonPath("$", hasSize(10)));
        mvc.perform(get("/api/weeks/{week}/review", thisWeek().previous()).with(guest)).andExpect(status().isOk());
    }

    @Test
    void reset_restoresGuestsAndSparesMembers() throws Exception {
        mvc.perform(put("/api/mission").with(guest).contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"Defaced\"}"))
                .andExpect(status().isOk());
        mvc.perform(put("/api/mission").with(member).contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"Mine\"}"))
                .andExpect(status().isOk());

        personCommands.resetGuestWorkspaces();

        mvc.perform(get("/api/mission").with(guest)).andExpect(jsonPath("$.text", startsWith("I live by principles")));
        mvc.perform(get("/api/mission").with(member)).andExpect(jsonPath("$.text").value("Mine"));
    }

    private WeekStart thisWeek() {
        return WeekStart.containing(LocalDate.now(clock));
    }
}
