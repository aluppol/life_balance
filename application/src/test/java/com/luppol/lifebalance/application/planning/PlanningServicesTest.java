package com.luppol.lifebalance.application.planning;

import com.luppol.lifebalance.application.fakes.PlannerStore;
import com.luppol.lifebalance.domain.NotFoundException;
import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalDetails;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.ActivityDetails;
import com.luppol.lifebalance.domain.planning.ActivityId;
import com.luppol.lifebalance.domain.planning.PlannedActivity;
import com.luppol.lifebalance.domain.planning.Quadrant;
import com.luppol.lifebalance.domain.planning.Tally;
import com.luppol.lifebalance.domain.planning.WeekScorecard;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanningServicesTest {
    private static final PersonId OWNER = new PersonId("owner");
    private static final WeekStart WEEK = new WeekStart(LocalDate.of(2026, 9, 21));

    private final PlannerStore store = new PlannerStore();
    private final PlanningCommands commands =
            new PlanningCommandService(store.activities(), store.lifeRoles(), store.goals());
    private final PlanningQueries queries = new PlanningQueryService(store.activities());
    private final LifeRole parent = LifeRole.personal(LifeRoleId.random(), OWNER, "Parent", "", 1);
    private final LifeRole engineer = LifeRole.personal(LifeRoleId.random(), OWNER, "Engineer", "", 2);
    private final Goal cycling = Goal.set(GoalId.random(), OWNER,
            new GoalDetails(parent.id(), "Teach cycling", "", Optional.empty(), Set.of()));

    PlanningServicesTest() {
        store.lifeRoles().add(parent);
        store.lifeRoles().add(engineer);
        store.goals().add(cycling);
    }

    @Test
    void plan_addsAnOpenActivityToTheWeek() {
        ActivityId id = plan(details(parent.id(), Optional.of(cycling.id())));

        PlannedActivity activity = queries.find(OWNER, id);
        assertThat(activity.week()).isEqualTo(WEEK);
        assertThat(activity.isCompleted()).isFalse();
        assertThat(queries.listWeek(OWNER, WEEK)).containsExactly(activity);
        assertThat(queries.listWeek(OWNER, WEEK.previous())).isEmpty();
    }

    @Test
    void plan_rejectsAnUnknownRole() {
        LifeRoleId unknown = LifeRoleId.random();

        assertThatThrownBy(() -> plan(details(unknown, Optional.empty())))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("Life role %s does not exist".formatted(unknown));
    }

    @Test
    void plan_rejectsAnUnknownGoal() {
        GoalId unknown = GoalId.random();

        assertThatThrownBy(() -> plan(details(parent.id(), Optional.of(unknown))))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("Goal %s does not exist".formatted(unknown));
    }

    @Test
    void plan_rejectsAGoalOfAnotherRole() {
        assertThatThrownBy(() -> plan(details(engineer.id(), Optional.of(cycling.id()))))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("Goal 'Teach cycling' belongs to another role");
    }

    @Test
    void revise_replacesTheDetails() {
        ActivityId id = plan(details(parent.id(), Optional.empty()));
        ActivityDetails revised = new ActivityDetails(engineer.id(), Optional.empty(), "Code review",
                Quadrant.IMPORTANT_URGENT, Optional.of(LocalDate.of(2026, 9, 22)));

        commands.revise(new ReviseActivity(id, OWNER, revised));

        assertThat(queries.find(OWNER, id).details()).isEqualTo(revised);
    }

    @Test
    void revise_checksReferences() {
        ActivityId id = plan(details(parent.id(), Optional.empty()));

        assertThatThrownBy(() -> commands.revise(new ReviseActivity(id, OWNER, details(LifeRoleId.random(), Optional.empty()))))
                .isInstanceOf(RuleViolationException.class);
    }

    @Test
    void completeAndReopen_toggleCompletion() {
        ActivityId id = plan(details(parent.id(), Optional.empty()));

        commands.complete(OWNER, id);
        assertThat(queries.find(OWNER, id).isCompleted()).isTrue();

        commands.reopen(OWNER, id);
        assertThat(queries.find(OWNER, id).isCompleted()).isFalse();
    }

    @Test
    void remove_deletesTheActivity() {
        ActivityId id = plan(details(parent.id(), Optional.empty()));

        commands.remove(OWNER, id);

        assertThat(queries.listWeek(OWNER, WEEK)).isEmpty();
    }

    @Test
    void remove_rejectsAnUnknownActivity() {
        ActivityId unknown = ActivityId.random();

        assertThatThrownBy(() -> commands.remove(OWNER, unknown)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void scorecard_talliesTheWeek() {
        ActivityId done = plan(details(parent.id(), Optional.empty()));
        plan(details(parent.id(), Optional.empty()));
        commands.complete(OWNER, done);

        WeekScorecard scorecard = queries.scorecard(OWNER, WEEK);

        assertThat(scorecard.overall()).isEqualTo(new Tally(2, 1));
        assertThat(scorecard.bigRocks()).isEqualTo(new Tally(2, 1));
    }

    private ActivityId plan(ActivityDetails details) {
        ActivityId id = ActivityId.random();
        commands.plan(new PlanActivity(id, OWNER, WEEK, details));
        return id;
    }

    private static ActivityDetails details(LifeRoleId role, Optional<GoalId> goal) {
        return new ActivityDetails(role, goal, "Bike practice", Quadrant.IMPORTANT_NOT_URGENT, Optional.empty());
    }
}
