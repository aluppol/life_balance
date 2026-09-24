package com.luppol.lifebalance.adapter.persistence.planning;

import com.luppol.lifebalance.adapter.persistence.PersistenceTest;
import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalDetails;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.planning.ActivityDetails;
import com.luppol.lifebalance.domain.planning.ActivityId;
import com.luppol.lifebalance.domain.planning.PlannedActivity;
import com.luppol.lifebalance.domain.planning.Quadrant;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.role.LifeRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PlannedActivityRepositoryAdapterTest extends PersistenceTest {
    private static final WeekStart WEEK = new WeekStart(LocalDate.of(2026, 9, 21));

    private LifeRole parent;
    private Goal cycling;

    @BeforeEach
    void enrollPeople() {
        enroll(OWNER, STRANGER);
        parent = addRole(OWNER, "Parent");
        cycling = Goal.set(GoalId.random(), OWNER, new GoalDetails(parent.id(), "Teach cycling", "", Optional.empty(), Set.of()));
        goals.add(cycling);
    }

    @Test
    void findAllInWeek_putsScheduledDaysFirst() {
        PlannedActivity someday = activity("Someday", Optional.empty());
        PlannedActivity friday = activity("Friday", Optional.of(LocalDate.of(2026, 9, 25)));
        PlannedActivity monday = activity("Monday", Optional.of(LocalDate.of(2026, 9, 21)));
        activities.add(someday);
        activities.add(friday);
        activities.add(monday);
        flushAndClear();

        assertThat(activities.findAllInWeek(OWNER, WEEK)).containsExactly(monday, friday, someday);
        assertThat(activities.findAllInWeek(OWNER, WEEK.previous())).isEmpty();
        assertThat(activities.findAllInWeek(STRANGER, WEEK)).isEmpty();
    }

    @Test
    void add_keepsGoalAndCompletion() {
        PlannedActivity activity = PlannedActivity.planned(ActivityId.random(), OWNER, WEEK,
                new ActivityDetails(parent.id(), Optional.of(cycling.id()), "Bike practice", Quadrant.IMPORTANT_NOT_URGENT,
                        Optional.of(LocalDate.of(2026, 9, 26)))).completed();
        activities.add(activity);
        flushAndClear();

        assertThat(activities.findById(OWNER, activity.id())).contains(activity);
        assertThat(activities.findById(STRANGER, activity.id())).isEmpty();
    }

    @Test
    void update_storesTheNewState() {
        PlannedActivity activity = activity("Bike practice", Optional.empty());
        activities.add(activity);
        flushAndClear();

        PlannedActivity revised = activity.revisedTo(new ActivityDetails(parent.id(), Optional.empty(), "Swim practice",
                Quadrant.IMPORTANT_URGENT, Optional.of(LocalDate.of(2026, 9, 27)))).completed();
        activities.update(revised);
        flushAndClear();

        assertThat(activities.findById(OWNER, activity.id())).contains(revised);
    }

    @Test
    void remove_onlyDeletesTheOwnersActivity() {
        PlannedActivity activity = activity("Bike practice", Optional.empty());
        activities.add(activity);
        flushAndClear();

        activities.remove(STRANGER, activity.id());
        assertThat(activities.findById(OWNER, activity.id())).isPresent();

        activities.remove(OWNER, activity.id());
        assertThat(activities.findById(OWNER, activity.id())).isEmpty();
    }

    @Test
    void removingTheGoal_keepsTheActivityWithoutIt() {
        PlannedActivity activity = PlannedActivity.planned(ActivityId.random(), OWNER, WEEK,
                new ActivityDetails(parent.id(), Optional.of(cycling.id()), "Bike practice", Quadrant.IMPORTANT_NOT_URGENT,
                        Optional.empty()));
        activities.add(activity);
        flushAndClear();

        goals.remove(OWNER, cycling.id());
        flushAndClear();

        assertThat(activities.findById(OWNER, activity.id())).get()
                .extracting(found -> found.details().goalId()).isEqualTo(Optional.empty());
    }

    private PlannedActivity activity(String title, Optional<LocalDate> day) {
        return PlannedActivity.planned(ActivityId.random(), OWNER, WEEK,
                new ActivityDetails(parent.id(), Optional.empty(), title, Quadrant.IMPORTANT_NOT_URGENT, day));
    }
}
