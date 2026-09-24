package com.luppol.lifebalance.domain.planning;

import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlannedActivityTest {
    private static final PersonId OWNER = new PersonId("owner");
    private static final WeekStart WEEK = new WeekStart(LocalDate.of(2026, 9, 21));
    private static final ActivityId ID = ActivityId.random();
    private static final LifeRoleId ROLE = LifeRoleId.random();

    @Test
    void planned_isNotCompleted() {
        assertThat(PlannedActivity.planned(ID, OWNER, WEEK, details(Quadrant.IMPORTANT_URGENT)).isCompleted()).isFalse();
    }

    @Test
    void completed_andReopened_toggleCompletion() {
        PlannedActivity activity = PlannedActivity.planned(ID, OWNER, WEEK, details(Quadrant.IMPORTANT_URGENT));
        assertThat(activity.completed().isCompleted()).isTrue();
        assertThat(activity.completed().reopened()).isEqualTo(activity);
    }

    @Test
    void isBigRock_onlyForImportantNotUrgent() {
        assertThat(PlannedActivity.planned(ID, OWNER, WEEK, details(Quadrant.IMPORTANT_NOT_URGENT)).isBigRock()).isTrue();
        assertThat(PlannedActivity.planned(ID, OWNER, WEEK, details(Quadrant.IMPORTANT_URGENT)).isBigRock()).isFalse();
        assertThat(PlannedActivity.planned(ID, OWNER, WEEK, details(Quadrant.NOT_IMPORTANT_URGENT)).isBigRock()).isFalse();
    }

    @Test
    void revisedTo_keepsCompletion() {
        PlannedActivity completed = PlannedActivity.planned(ID, OWNER, WEEK, details(Quadrant.IMPORTANT_URGENT)).completed();
        ActivityDetails revised = details(Quadrant.NOT_IMPORTANT_NOT_URGENT);
        assertThat(completed.revisedTo(revised)).isEqualTo(new PlannedActivity(ID, OWNER, WEEK, revised, true));
    }

    @Test
    void activity_acceptsADateInsideItsWeek() {
        ActivityDetails onSunday = scheduled(LocalDate.of(2026, 9, 27));
        assertThat(PlannedActivity.planned(ID, OWNER, WEEK, onSunday).details().scheduledOn()).contains(LocalDate.of(2026, 9, 27));
    }

    @Test
    void activity_rejectsADateOutsideItsWeek() {
        ActivityDetails nextMonday = scheduled(LocalDate.of(2026, 9, 28));
        assertThatThrownBy(() -> PlannedActivity.planned(ID, OWNER, WEEK, nextMonday))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("2026-09-28 is outside the week starting 2026-09-21");
    }

    @Test
    void activity_requiresItsParts() {
        ActivityDetails details = details(Quadrant.IMPORTANT_URGENT);
        assertThatThrownBy(() -> PlannedActivity.planned(null, OWNER, WEEK, details)).hasMessage("Activity id is required");
        assertThatThrownBy(() -> PlannedActivity.planned(ID, null, WEEK, details)).hasMessage("Activity owner is required");
        assertThatThrownBy(() -> PlannedActivity.planned(ID, OWNER, null, details)).hasMessage("Activity week is required");
        assertThatThrownBy(() -> PlannedActivity.planned(ID, OWNER, WEEK, null)).hasMessage("Activity details is required");
    }

    @Test
    void details_validateTheirFields() {
        Optional<GoalId> noGoal = Optional.empty();
        Optional<LocalDate> noDate = Optional.empty();
        assertThatThrownBy(() -> new ActivityDetails(null, noGoal, "t", Quadrant.IMPORTANT_URGENT, noDate))
                .hasMessage("Activity role is required");
        assertThatThrownBy(() -> new ActivityDetails(ROLE, null, "t", Quadrant.IMPORTANT_URGENT, noDate))
                .hasMessage("Activity goal is required");
        assertThatThrownBy(() -> new ActivityDetails(ROLE, noGoal, "t".repeat(201), Quadrant.IMPORTANT_URGENT, noDate))
                .hasMessage("Activity title must be at most 200 characters");
        assertThatThrownBy(() -> new ActivityDetails(ROLE, noGoal, "t", null, noDate))
                .hasMessage("Activity quadrant is required");
        assertThatThrownBy(() -> new ActivityDetails(ROLE, noGoal, "t", Quadrant.IMPORTANT_URGENT, null))
                .hasMessage("Activity date is required");
    }

    @Test
    void id_isRandomAndPrintable() {
        ActivityId random = ActivityId.random();
        assertThat(random).isNotEqualTo(ActivityId.random()).hasToString(random.value().toString());
        assertThatThrownBy(() -> new ActivityId(null)).isInstanceOf(RuleViolationException.class);
    }

    private static ActivityDetails details(Quadrant quadrant) {
        return new ActivityDetails(ROLE, Optional.empty(), "Plan the week", quadrant, Optional.empty());
    }

    private static ActivityDetails scheduled(LocalDate date) {
        return new ActivityDetails(ROLE, Optional.empty(), "Long run", Quadrant.IMPORTANT_NOT_URGENT, Optional.of(date));
    }
}
