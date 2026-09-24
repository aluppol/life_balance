package com.luppol.lifebalance.application.demo;

import com.luppol.lifebalance.application.fakes.PlannerStore;
import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalStatus;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.PlannedActivity;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.review.RenewalDimension;
import com.luppol.lifebalance.domain.review.WeeklyReview;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.value.CoreValue;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class DemoWorkspaceTest {
    static final Clock WEDNESDAY = Clock.fixed(Instant.parse("2026-09-23T15:00:00Z"), ZoneOffset.UTC);
    private static final PersonId GUEST = new PersonId("guest");
    private static final WeekStart THIS_WEEK = new WeekStart(LocalDate.of(2026, 9, 21));

    private final PlannerStore store = new PlannerStore();
    private final DemoWorkspace workspace = new DemoWorkspace(store.repositories(), WEDNESDAY);

    DemoWorkspaceTest() {
        store.lifeRoles().add(LifeRole.sharpenTheSaw(GUEST));
        workspace.furnish(GUEST);
    }

    @Test
    void furnish_writesTheMissionStatement() {
        assertThat(store.missionStatements().findByOwner(GUEST)).get()
                .extracting(statement -> statement.text()).asString().startsWith("I live by principles I choose");
    }

    @Test
    void furnish_addsRankedValues() {
        assertThat(store.coreValues().findAllByOwner(GUEST)).extracting(CoreValue::name, CoreValue::position)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Integrity", 0),
                        org.assertj.core.groups.Tuple.tuple("Family", 1),
                        org.assertj.core.groups.Tuple.tuple("Growth", 2),
                        org.assertj.core.groups.Tuple.tuple("Health", 3));
    }

    @Test
    void furnish_addsRolesAfterSharpenTheSaw() {
        assertThat(store.lifeRoles().findAllByOwner(GUEST)).extracting(LifeRole::name)
                .containsExactly("Sharpen the Saw", "Parent", "Partner", "Engineer", "Friend");
    }

    @Test
    void furnish_setsGoalsWithRolesValuesAndDueDates() {
        Map<String, Goal> goals = goalsByTitle();
        Goal run = goals.get("Run a 10K under 55 minutes");
        assertThat(goals).hasSize(6);
        assertThat(run.details().roleId()).isEqualTo(roleNamed("Sharpen the Saw").id());
        assertThat(run.details().dueOn()).contains(LocalDate.of(2026, 11, 22));
        assertThat(run.details().valueIds()).hasSize(2);
        assertThat(goals.get("Call an old friend every week").details().dueOn()).isEmpty();
        assertThat(goals.get("Re-read The 7 Habits").status()).isEqualTo(GoalStatus.ACHIEVED);
    }

    @Test
    void furnish_plansThisWeek() {
        List<PlannedActivity> thisWeek = store.activities().findAllInWeek(GUEST, THIS_WEEK);
        PlannedActivity bike = activityTitled(thisWeek, "Bike practice in the park");
        assertThat(thisWeek).hasSize(10);
        assertThat(bike.details().scheduledOn()).contains(LocalDate.of(2026, 9, 26));
        assertThat(bike.details().goalId()).contains(goalsByTitle().get("Teach Mia to ride a bike").id());
        assertThat(bike.details().roleId()).isEqualTo(roleNamed("Parent").id());
        assertThat(bike.isCompleted()).isFalse();
        assertThat(activityTitled(thisWeek, "Answer the vendor survey").details().scheduledOn()).isEmpty();
        assertThat(thisWeek.stream().filter(PlannedActivity::isCompleted)).hasSize(4);
    }

    @Test
    void furnish_reviewsLastWeek() {
        WeekStart lastWeek = THIS_WEEK.previous();
        Optional<WeeklyReview> review = store.reviews().findByWeek(GUEST, lastWeek);
        assertThat(store.activities().findAllInWeek(GUEST, lastWeek)).hasSize(6);
        assertThat(review).get().extracting(WeeklyReview::renewedDimensions).isEqualTo(
                java.util.Set.of(RenewalDimension.PHYSICAL, RenewalDimension.MENTAL, RenewalDimension.SOCIAL_EMOTIONAL));
        assertThat(review.orElseThrow().lessons()).startsWith("Big rocks first works");
    }

    private Map<String, Goal> goalsByTitle() {
        return store.goals().findAllByOwner(GUEST).stream()
                .collect(Collectors.toMap(goal -> goal.details().title(), goal -> goal));
    }

    private LifeRole roleNamed(String name) {
        return store.lifeRoles().findAllByOwner(GUEST).stream().filter(role -> role.isNamed(name)).findFirst().orElseThrow();
    }

    private static PlannedActivity activityTitled(List<PlannedActivity> activities, String title) {
        return activities.stream().filter(activity -> activity.details().title().equals(title)).findFirst().orElseThrow();
    }
}
