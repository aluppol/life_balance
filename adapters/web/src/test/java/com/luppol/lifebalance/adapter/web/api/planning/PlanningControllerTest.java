package com.luppol.lifebalance.adapter.web.api.planning;

import com.luppol.lifebalance.adapter.web.WebTest;
import com.luppol.lifebalance.application.planning.PlanActivity;
import com.luppol.lifebalance.application.planning.ReviseActivity;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.planning.ActivityDetails;
import com.luppol.lifebalance.domain.planning.ActivityId;
import com.luppol.lifebalance.domain.planning.PlannedActivity;
import com.luppol.lifebalance.domain.planning.Quadrant;
import com.luppol.lifebalance.domain.planning.WeekScorecard;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlanningControllerTest extends WebTest {
    private static final WeekStart WEEK = new WeekStart(LocalDate.of(2026, 9, 21));

    private final LifeRoleId role = LifeRoleId.random();
    private final GoalId goal = GoalId.random();
    private final ActivityDetails details = new ActivityDetails(role, Optional.of(goal), "Bike practice",
            Quadrant.IMPORTANT_NOT_URGENT, Optional.of(LocalDate.of(2026, 9, 26)));
    private final PlannedActivity activity = PlannedActivity.planned(ActivityId.random(), OWNER, WEEK, details);

    @Test
    void listWeek_describesEachActivity() throws Exception {
        when(planningQueries.listWeek(OWNER, WEEK)).thenReturn(List.of(activity.completed()));

        mvc.perform(get("/api/weeks/2026-09-21/activities").with(member()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(activity.id().toString()))
                .andExpect(jsonPath("$[0].weekStart").value("2026-09-21"))
                .andExpect(jsonPath("$[0].roleId").value(role.toString()))
                .andExpect(jsonPath("$[0].goalId").value(goal.toString()))
                .andExpect(jsonPath("$[0].quadrant").value("IMPORTANT_NOT_URGENT"))
                .andExpect(jsonPath("$[0].scheduledOn").value("2026-09-26"))
                .andExpect(jsonPath("$[0].isCompleted").value(true));
    }

    @Test
    void listWeek_rejectsAWeekThatDoesNotStartOnMonday() throws Exception {
        mvc.perform(get("/api/weeks/2026-09-23/activities").with(member())).andExpect(status().isBadRequest());
    }

    @Test
    void plan_addsTheActivityToTheWeek() throws Exception {
        when(planningQueries.find(any(), any())).thenReturn(activity);

        mvc.perform(post("/api/weeks/2026-09-21/activities").with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"roleId":"%s","goalId":"%s","title":"Bike practice",
                                 "quadrant":"IMPORTANT_NOT_URGENT","scheduledOn":"2026-09-26"}
                                """.formatted(role, goal)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/activities/")));

        ArgumentCaptor<PlanActivity> planned = ArgumentCaptor.forClass(PlanActivity.class);
        verify(planningCommands).plan(planned.capture());
        assertThat(planned.getValue().week()).isEqualTo(WEEK);
        assertThat(planned.getValue().details()).isEqualTo(details);
    }

    @Test
    void plan_requiresAQuadrant() throws Exception {
        mvc.perform(post("/api/weeks/2026-09-21/activities").with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleId\":\"%s\",\"title\":\"x\"}".formatted(role)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.quadrant").exists());
    }

    @Test
    void revise_replacesTheDetails() throws Exception {
        when(planningQueries.find(OWNER, activity.id())).thenReturn(activity);

        mvc.perform(put("/api/activities/{id}", activity.id().value()).with(member())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleId\":\"%s\",\"title\":\"Swim\",\"quadrant\":\"IMPORTANT_URGENT\"}".formatted(role)))
                .andExpect(status().isOk());

        verify(planningCommands).revise(new ReviseActivity(activity.id(), OWNER,
                new ActivityDetails(role, Optional.empty(), "Swim", Quadrant.IMPORTANT_URGENT, Optional.empty())));
    }

    @Test
    void completion_canBeMarkedAndCleared() throws Exception {
        when(planningQueries.find(OWNER, activity.id())).thenReturn(activity);

        mvc.perform(put("/api/activities/{id}/completion", activity.id().value()).with(member())).andExpect(status().isOk());
        mvc.perform(delete("/api/activities/{id}/completion", activity.id().value()).with(member())).andExpect(status().isOk());

        verify(planningCommands).complete(OWNER, activity.id());
        verify(planningCommands).reopen(OWNER, activity.id());
    }

    @Test
    void remove_deletesTheActivity() throws Exception {
        mvc.perform(delete("/api/activities/{id}", activity.id().value()).with(member())).andExpect(status().isNoContent());

        verify(planningCommands).remove(OWNER, activity.id());
    }

    @Test
    void scorecard_listsEveryQuadrantAndRole() throws Exception {
        when(planningQueries.scorecard(OWNER, WEEK)).thenReturn(WeekScorecard.of(WEEK, List.of(activity.completed())));

        mvc.perform(get("/api/weeks/2026-09-21/scorecard").with(member()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weekStart").value("2026-09-21"))
                .andExpect(jsonPath("$.overall.planned").value(1))
                .andExpect(jsonPath("$.overall.completed").value(1))
                .andExpect(jsonPath("$.bigRocks.completed").value(1))
                .andExpect(jsonPath("$.byQuadrant.IMPORTANT_URGENT.planned").value(0))
                .andExpect(jsonPath("$.byQuadrant.IMPORTANT_NOT_URGENT.planned").value(1))
                .andExpect(jsonPath("$.byRole[0].roleId").value(role.toString()))
                .andExpect(jsonPath("$.byRole[0].completed").value(1));
    }
}
