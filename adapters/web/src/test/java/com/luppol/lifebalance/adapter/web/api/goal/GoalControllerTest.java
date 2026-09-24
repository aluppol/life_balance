package com.luppol.lifebalance.adapter.web.api.goal;

import com.luppol.lifebalance.adapter.web.WebTest;
import com.luppol.lifebalance.application.goal.ReviseGoal;
import com.luppol.lifebalance.application.goal.SetGoal;
import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalDetails;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.value.CoreValueId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GoalControllerTest extends WebTest {
    private final LifeRoleId role = LifeRoleId.random();
    private final CoreValueId family = CoreValueId.random();
    private final GoalDetails details = new GoalDetails(role, "Teach cycling", "Before her birthday",
            Optional.of(LocalDate.of(2026, 10, 31)), Set.of(family));
    private final Goal goal = Goal.set(GoalId.random(), OWNER, details);

    @Test
    void listAll_describesEachGoal() throws Exception {
        when(goalQueries.listAll(OWNER)).thenReturn(List.of(goal.achieved()));

        mvc.perform(get("/api/goals").with(member()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roleId").value(role.toString()))
                .andExpect(jsonPath("$[0].title").value("Teach cycling"))
                .andExpect(jsonPath("$[0].dueOn").value("2026-10-31"))
                .andExpect(jsonPath("$[0].status").value("ACHIEVED"))
                .andExpect(jsonPath("$[0].valueIds[0]").value(family.toString()));
    }

    @Test
    void set_createsAnActiveGoal() throws Exception {
        when(goalQueries.find(any(), any())).thenReturn(goal);

        mvc.perform(post("/api/goals").with(member()).contentType(MediaType.APPLICATION_JSON).content("""
                        {"roleId":"%s","title":"Teach cycling","description":"Before her birthday",
                         "dueOn":"2026-10-31","valueIds":["%s"]}
                        """.formatted(role, family)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        ArgumentCaptor<SetGoal> set = ArgumentCaptor.forClass(SetGoal.class);
        verify(goalCommands).set(set.capture());
        assertThat(set.getValue().details()).isEqualTo(details);
        assertThat(set.getValue().owner()).isEqualTo(OWNER);
    }

    @Test
    void set_acceptsAGoalWithoutDateOrValues() throws Exception {
        when(goalQueries.find(any(), any())).thenReturn(goal);

        mvc.perform(post("/api/goals").with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleId\":\"%s\",\"title\":\"Call a friend\"}".formatted(role)))
                .andExpect(status().isCreated());

        ArgumentCaptor<SetGoal> set = ArgumentCaptor.forClass(SetGoal.class);
        verify(goalCommands).set(set.capture());
        assertThat(set.getValue().details()).isEqualTo(new GoalDetails(role, "Call a friend", "", Optional.empty(), Set.of()));
    }

    @Test
    void set_reportsAForeignRoleAsUnprocessable() throws Exception {
        doThrow(new RuleViolationException("Life role %s does not exist".formatted(role))).when(goalCommands).set(any());

        mvc.perform(post("/api/goals").with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleId\":\"%s\",\"title\":\"Mine\"}".formatted(role)))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void set_requiresARole() throws Exception {
        mvc.perform(post("/api/goals").with(member()).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.roleId").exists());
    }

    @Test
    void revise_replacesTheDetails() throws Exception {
        when(goalQueries.find(OWNER, goal.id())).thenReturn(goal);

        mvc.perform(put("/api/goals/{id}", goal.id().value()).with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleId\":\"%s\",\"title\":\"Teach swimming\"}".formatted(role)))
                .andExpect(status().isOk());

        verify(goalCommands).revise(new ReviseGoal(goal.id(), OWNER,
                new GoalDetails(role, "Teach swimming", "", Optional.empty(), Set.of())));
    }

    @Test
    void changeStatus_achievesDropsAndReopens() throws Exception {
        when(goalQueries.find(OWNER, goal.id())).thenReturn(goal);

        changeStatus("ACHIEVED");
        changeStatus("DROPPED");
        changeStatus("ACTIVE");

        verify(goalCommands).achieve(OWNER, goal.id());
        verify(goalCommands).drop(OWNER, goal.id());
        verify(goalCommands).reopen(OWNER, goal.id());
    }

    @Test
    void changeStatus_rejectsAnUnknownStatus() throws Exception {
        mvc.perform(put("/api/goals/{id}/status", goal.id().value()).with(member())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"PAUSED\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void remove_deletesTheGoal() throws Exception {
        mvc.perform(delete("/api/goals/{id}", goal.id().value()).with(member())).andExpect(status().isNoContent());

        verify(goalCommands).remove(OWNER, goal.id());
    }

    private void changeStatus(String status) throws Exception {
        mvc.perform(put("/api/goals/{id}/status", goal.id().value()).with(member())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"%s\"}".formatted(status)))
                .andExpect(status().isOk());
    }
}
