package com.luppol.lifebalance.domain.goal;

import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.value.CoreValueId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GoalTest {
    private static final PersonId OWNER = new PersonId("owner");
    private static final GoalId ID = GoalId.random();
    private static final LifeRoleId ROLE = LifeRoleId.random();
    private static final GoalDetails DETAILS = new GoalDetails(ROLE, "Run a marathon", "Under four hours",
            Optional.of(LocalDate.of(2026, 10, 31)), Set.of(CoreValueId.random()));

    @Test
    void set_startsActive() {
        Goal goal = Goal.set(ID, OWNER, DETAILS);
        assertThat(goal.status()).isEqualTo(GoalStatus.ACTIVE);
        assertThat(goal.isActive()).isTrue();
    }

    @Test
    void achieved_endsAnActiveGoal() {
        Goal achieved = Goal.set(ID, OWNER, DETAILS).achieved();
        assertThat(achieved.status()).isEqualTo(GoalStatus.ACHIEVED);
        assertThat(achieved.isActive()).isFalse();
    }

    @Test
    void dropped_endsAnActiveGoal() {
        assertThat(Goal.set(ID, OWNER, DETAILS).dropped().status()).isEqualTo(GoalStatus.DROPPED);
    }

    @Test
    void achieved_rejectsAnEndedGoal() {
        Goal dropped = Goal.set(ID, OWNER, DETAILS).dropped();
        assertThatThrownBy(dropped::achieved)
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("Only an active goal can be achieved");
    }

    @Test
    void dropped_rejectsAnEndedGoal() {
        Goal achieved = Goal.set(ID, OWNER, DETAILS).achieved();
        assertThatThrownBy(achieved::dropped).hasMessage("Only an active goal can be dropped");
    }

    @Test
    void reopened_restartsAnEndedGoal() {
        assertThat(Goal.set(ID, OWNER, DETAILS).achieved().reopened().status()).isEqualTo(GoalStatus.ACTIVE);
        assertThat(Goal.set(ID, OWNER, DETAILS).dropped().reopened().status()).isEqualTo(GoalStatus.ACTIVE);
    }

    @Test
    void reopened_rejectsAnActiveGoal() {
        assertThatThrownBy(() -> Goal.set(ID, OWNER, DETAILS).reopened())
                .hasMessage("Only an achieved or dropped goal can be reopened");
    }

    @Test
    void revisedTo_keepsIdentityAndStatus() {
        GoalDetails revised = new GoalDetails(ROLE, "Run a half marathon", "", Optional.empty(), Set.of());
        Goal goal = Goal.set(ID, OWNER, DETAILS).achieved().revisedTo(revised);
        assertThat(goal).isEqualTo(new Goal(ID, OWNER, revised, GoalStatus.ACHIEVED));
    }

    @Test
    void goal_requiresItsParts() {
        assertThatThrownBy(() -> new Goal(null, OWNER, DETAILS, GoalStatus.ACTIVE)).hasMessage("Goal id is required");
        assertThatThrownBy(() -> new Goal(ID, null, DETAILS, GoalStatus.ACTIVE)).hasMessage("Goal owner is required");
        assertThatThrownBy(() -> new Goal(ID, OWNER, null, GoalStatus.ACTIVE)).hasMessage("Goal details is required");
        assertThatThrownBy(() -> new Goal(ID, OWNER, DETAILS, null)).hasMessage("Goal status is required");
    }

    @Test
    void details_validateTheirFields() {
        assertThatThrownBy(() -> new GoalDetails(null, "t", "", Optional.empty(), Set.of())).hasMessage("Goal role is required");
        assertThatThrownBy(() -> new GoalDetails(ROLE, " ", "", Optional.empty(), Set.of())).isInstanceOf(RuleViolationException.class);
        assertThatThrownBy(() -> new GoalDetails(ROLE, "t".repeat(201), "", Optional.empty(), Set.of()))
                .hasMessage("Goal title must be at most 200 characters");
        assertThatThrownBy(() -> new GoalDetails(ROLE, "t", "d".repeat(2001), Optional.empty(), Set.of()))
                .hasMessage("Goal description must be at most 2000 characters");
        assertThatThrownBy(() -> new GoalDetails(ROLE, "t", "", null, Set.of())).hasMessage("Goal due date is required");
        assertThatThrownBy(() -> new GoalDetails(ROLE, "t", "", Optional.empty(), null)).hasMessage("Goal values is required");
    }

    @Test
    void details_copyTheirValues() {
        Set<CoreValueId> values = new HashSet<>(Set.of(CoreValueId.random()));
        GoalDetails details = new GoalDetails(ROLE, "t", "", Optional.empty(), values);
        values.add(CoreValueId.random());
        assertThat(details.valueIds()).hasSize(1);
    }

    @Test
    void id_isRandomAndPrintable() {
        GoalId random = GoalId.random();
        assertThat(random).isNotEqualTo(GoalId.random()).hasToString(random.value().toString());
        assertThatThrownBy(() -> new GoalId(null)).isInstanceOf(RuleViolationException.class);
    }
}
