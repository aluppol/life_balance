package com.luppol.lifebalance.adapter.persistence.goal;

import com.luppol.lifebalance.adapter.persistence.PersistenceTest;
import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalDetails;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GoalRepositoryAdapterTest extends PersistenceTest {
    private LifeRole parent;
    private final CoreValue family = new CoreValue(CoreValueId.random(), OWNER, "Family", "", 0);
    private final CoreValue growth = new CoreValue(CoreValueId.random(), OWNER, "Growth", "", 1);

    @BeforeEach
    void enrollPeople() {
        enroll(OWNER, STRANGER);
        parent = addRole(OWNER, "Parent");
        coreValues.add(family);
        coreValues.add(growth);
    }

    @Test
    void add_storesDetailsAndValues() {
        Goal goal = goal("Teach cycling", Optional.of(LocalDate.of(2026, 12, 24)), Set.of(family.id(), growth.id()));
        goals.add(goal);
        flushAndClear();

        assertThat(goals.findById(OWNER, goal.id())).contains(goal);
        assertThat(goals.findById(STRANGER, goal.id())).isEmpty();
    }

    @Test
    void findAllByOwner_listsInCreationOrder() {
        Goal first = goal("B first", Optional.empty(), Set.of());
        goals.add(first);
        flushAndClear();
        Goal second = goal("A second", Optional.empty(), Set.of());
        goals.add(second);
        flushAndClear();

        assertThat(goals.findAllByOwner(OWNER)).containsExactly(first, second);
    }

    @Test
    void update_replacesDetailsStatusAndValues() {
        Goal goal = goal("Teach cycling", Optional.empty(), Set.of(family.id()));
        goals.add(goal);
        flushAndClear();

        Goal revised = goal.revisedTo(new GoalDetails(parent.id(), "Teach swimming", "In the lake",
                Optional.of(LocalDate.of(2027, 6, 1)), Set.of(growth.id()))).achieved();
        goals.update(revised);
        flushAndClear();

        assertThat(goals.findById(OWNER, goal.id())).contains(revised);
    }

    @Test
    void remove_onlyDeletesTheOwnersGoal() {
        Goal goal = goal("Teach cycling", Optional.empty(), Set.of(family.id()));
        goals.add(goal);
        flushAndClear();

        goals.remove(STRANGER, goal.id());
        assertThat(goals.findById(OWNER, goal.id())).isPresent();

        goals.remove(OWNER, goal.id());
        flushAndClear();
        assertThat(goals.findById(OWNER, goal.id())).isEmpty();
    }

    @Test
    void removingAValue_unlinksItFromGoals() {
        Goal goal = goal("Teach cycling", Optional.empty(), Set.of(family.id(), growth.id()));
        goals.add(goal);
        flushAndClear();

        coreValues.remove(OWNER, growth.id());
        flushAndClear();

        assertThat(goals.findById(OWNER, goal.id())).get().extracting(found -> found.details().valueIds())
                .isEqualTo(Set.of(family.id()));
    }

    @Test
    void database_rejectsAGoalThatPointsAtAnotherPersonsRole() {
        LifeRole foreign = addRole(STRANGER, "Parent");
        Goal goal = Goal.set(GoalId.random(), OWNER,
                new GoalDetails(foreign.id(), "Borrowed role", "", Optional.empty(), Set.of()));

        goals.add(goal);

        assertThatThrownBy(this::flushAndClear).isInstanceOf(ConstraintViolationException.class);
    }

    private Goal goal(String title, Optional<LocalDate> dueOn, Set<CoreValueId> values) {
        return Goal.set(GoalId.random(), OWNER, new GoalDetails(parent.id(), title, "Why it matters", dueOn, values));
    }
}
