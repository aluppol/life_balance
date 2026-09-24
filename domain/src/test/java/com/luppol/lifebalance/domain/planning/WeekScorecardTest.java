package com.luppol.lifebalance.domain.planning;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class WeekScorecardTest {
    private static final PersonId OWNER = new PersonId("owner");
    private static final WeekStart WEEK = new WeekStart(LocalDate.of(2026, 9, 21));
    private static final LifeRoleId PARENT = LifeRoleId.random();
    private static final LifeRoleId ENGINEER = LifeRoleId.random();

    @Test
    void scorecard_ofAnEmptyWeek_countsNothing() {
        WeekScorecard scorecard = WeekScorecard.of(WEEK, List.of());
        assertThat(scorecard.week()).isEqualTo(WEEK);
        assertThat(scorecard.overall()).isEqualTo(Tally.NONE);
        assertThat(scorecard.bigRocks()).isEqualTo(Tally.NONE);
        assertThat(scorecard.byQuadrant()).isEmpty();
        assertThat(scorecard.byRole()).isEmpty();
    }

    @Test
    void scorecard_talliesPlannedAndCompletedActivities() {
        WeekScorecard scorecard = WeekScorecard.of(WEEK, List.of(
                activity(PARENT, Quadrant.IMPORTANT_NOT_URGENT, true),
                activity(PARENT, Quadrant.IMPORTANT_NOT_URGENT, false),
                activity(ENGINEER, Quadrant.IMPORTANT_URGENT, true),
                activity(ENGINEER, Quadrant.NOT_IMPORTANT_URGENT, false)));

        assertThat(scorecard.overall()).isEqualTo(new Tally(4, 2));
        assertThat(scorecard.bigRocks()).isEqualTo(new Tally(2, 1));
        assertThat(scorecard.byQuadrant()).isEqualTo(Map.of(
                Quadrant.IMPORTANT_NOT_URGENT, new Tally(2, 1),
                Quadrant.IMPORTANT_URGENT, new Tally(1, 1),
                Quadrant.NOT_IMPORTANT_URGENT, new Tally(1, 0)));
        assertThat(scorecard.byRole()).isEqualTo(Map.of(PARENT, new Tally(2, 1), ENGINEER, new Tally(2, 1)));
    }

    @Test
    void tally_addsUp() {
        assertThat(new Tally(2, 1).plus(new Tally(3, 3))).isEqualTo(new Tally(5, 4));
    }

    private static PlannedActivity activity(LifeRoleId role, Quadrant quadrant, boolean isCompleted) {
        ActivityDetails details = new ActivityDetails(role, Optional.empty(), "Activity", quadrant, Optional.empty());
        return new PlannedActivity(ActivityId.random(), OWNER, WEEK, details, isCompleted);
    }
}
