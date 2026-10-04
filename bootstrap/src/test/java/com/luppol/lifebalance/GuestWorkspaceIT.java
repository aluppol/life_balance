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
    private static final String DEMO_MISSION = "I live by principles";

    private final String guestAccount = "guest-" + UUID.randomUUID();
    private final RequestPostProcessor visit = newVisit();
    private final RequestPostProcessor member = person("member-" + UUID.randomUUID(), "USER");

    @Autowired
    private PersonCommands personCommands;

    @Autowired
    private Clock clock;

    @Test
    void guest_findsAFurnishedPlanner() throws Exception {
        mvc.perform(get("/api/mission").with(visit))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", startsWith(DEMO_MISSION)));
        mvc.perform(get("/api/roles").with(visit)).andExpect(jsonPath("$", hasSize(5)));
        mvc.perform(get("/api/goals").with(visit)).andExpect(jsonPath("$", hasSize(6)));
        mvc.perform(get("/api/weeks/{week}/activities", thisWeek()).with(visit)).andExpect(jsonPath("$", hasSize(10)));
        mvc.perform(get("/api/weeks/{week}/review", thisWeek().previous()).with(visit)).andExpect(status().isOk());
    }

    @Test
    void visitsOfOneGuestAccount_haveSeparateWorkspaces() throws Exception {
        RequestPostProcessor nextVisit = newVisit();
        deface(visit);

        mvc.perform(get("/api/mission").with(nextVisit)).andExpect(jsonPath("$.text", startsWith(DEMO_MISSION)));
        mvc.perform(get("/api/mission").with(visit)).andExpect(jsonPath("$.text").value("Defaced"));
    }

    @Test
    void visitsBeyondTheLimit_evictTheOldestWorkspace() throws Exception {
        deface(visit);

        for (int visitsStarted = 0; visitsStarted < GUEST_WORKSPACE_LIMIT; visitsStarted++) {
            mvc.perform(get("/api/me").with(newVisit())).andExpect(status().isOk());
        }

        mvc.perform(get("/api/mission").with(visit)).andExpect(jsonPath("$.text", startsWith(DEMO_MISSION)));
    }

    @Test
    void reset_removesGuestWorkspacesAndSparesMembers() throws Exception {
        deface(visit);
        mvc.perform(put("/api/mission").with(member).contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"Mine\"}"))
                .andExpect(status().isOk());

        personCommands.resetGuestWorkspaces();

        mvc.perform(get("/api/mission").with(visit)).andExpect(jsonPath("$.text", startsWith(DEMO_MISSION)));
        mvc.perform(get("/api/mission").with(member)).andExpect(jsonPath("$.text").value("Mine"));
    }

    private RequestPostProcessor newVisit() {
        return guestVisit(guestAccount, "session-" + UUID.randomUUID());
    }

    private void deface(RequestPostProcessor guest) throws Exception {
        mvc.perform(put("/api/mission").with(guest).contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"Defaced\"}"))
                .andExpect(status().isOk());
    }

    private WeekStart thisWeek() {
        return WeekStart.containing(LocalDate.now(clock));
    }
}
