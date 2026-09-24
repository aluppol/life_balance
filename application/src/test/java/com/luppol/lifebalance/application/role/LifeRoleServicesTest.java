package com.luppol.lifebalance.application.role;

import com.luppol.lifebalance.application.fakes.PlannerStore;
import com.luppol.lifebalance.domain.ConflictException;
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
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.role.LifeRoleKind;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LifeRoleServicesTest {
    private static final PersonId OWNER = new PersonId("owner");

    private final PlannerStore store = new PlannerStore();
    private final LifeRoleCommands commands = new LifeRoleCommandService(store.lifeRoles());
    private final LifeRoleQueries queries = new LifeRoleQueryService(store.lifeRoles());
    private final LifeRole saw = LifeRole.sharpenTheSaw(OWNER);

    LifeRoleServicesTest() {
        store.lifeRoles().add(saw);
    }

    @Test
    void add_appendsAPersonalRoleAfterTheBuiltInOne() {
        LifeRoleId parent = add("Parent");

        assertThat(queries.listAll(OWNER)).extracting(LifeRole::id).containsExactly(saw.id(), parent);
        assertThat(queries.find(OWNER, parent).kind()).isEqualTo(LifeRoleKind.PERSONAL);
        assertThat(queries.find(OWNER, parent).position()).isEqualTo(1);
    }

    @Test
    void add_rejectsADuplicateName() {
        assertThatThrownBy(() -> add("sharpen the saw"))
                .isInstanceOf(ConflictException.class)
                .hasMessage("A life role named 'sharpen the saw' already exists");
    }

    @Test
    void revise_renamesTheRole() {
        LifeRoleId parent = add("Parent");

        commands.revise(new ReviseLifeRole(parent, OWNER, "Father", "Be there"));

        assertThat(queries.find(OWNER, parent).name()).isEqualTo("Father");
        assertThat(queries.find(OWNER, parent).description()).isEqualTo("Be there");
    }

    @Test
    void revise_rejectsTheNameOfAnotherRole() {
        LifeRoleId parent = add("Parent");

        assertThatThrownBy(() -> commands.revise(new ReviseLifeRole(parent, OWNER, "Sharpen the Saw", "")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void revise_keepsItsOwnName() {
        LifeRoleId parent = add("Parent");

        commands.revise(new ReviseLifeRole(parent, OWNER, "PARENT", ""));

        assertThat(queries.find(OWNER, parent).name()).isEqualTo("PARENT");
    }

    @Test
    void reorder_rewritesPositions() {
        LifeRoleId parent = add("Parent");

        commands.reorder(new ReorderLifeRoles(OWNER, List.of(parent, saw.id())));

        assertThat(queries.listAll(OWNER)).extracting(LifeRole::id).containsExactly(parent, saw.id());
    }

    @Test
    void reorder_rejectsAForeignRole() {
        assertThatThrownBy(() -> commands.reorder(new ReorderLifeRoles(OWNER, List.of(LifeRoleId.random()))))
                .isInstanceOf(RuleViolationException.class);
    }

    @Test
    void remove_deletesAnUnusedPersonalRole() {
        LifeRoleId parent = add("Parent");

        commands.remove(OWNER, parent);

        assertThat(queries.listAll(OWNER)).containsExactly(saw);
    }

    @Test
    void remove_rejectsTheBuiltInRole() {
        assertThatThrownBy(() -> commands.remove(OWNER, saw.id()))
                .isInstanceOf(ConflictException.class)
                .hasMessage("The Sharpen the Saw role cannot be removed");
    }

    @Test
    void remove_rejectsARoleWithGoals() {
        LifeRoleId parent = add("Parent");
        GoalDetails details = new GoalDetails(parent, "Teach cycling", "", Optional.empty(), Set.of());
        store.goals().add(Goal.set(GoalId.random(), OWNER, details));

        assertThatThrownBy(() -> commands.remove(OWNER, parent))
                .isInstanceOf(ConflictException.class)
                .hasMessage("The Parent role still has goals or activities");
    }

    @Test
    void remove_rejectsARoleWithActivities() {
        LifeRoleId parent = add("Parent");
        ActivityDetails details = new ActivityDetails(parent, Optional.empty(), "Picnic", Quadrant.IMPORTANT_NOT_URGENT,
                Optional.empty());
        WeekStart week = new WeekStart(LocalDate.of(2026, 9, 21));
        store.activities().add(PlannedActivity.planned(ActivityId.random(), OWNER, week, details));

        assertThatThrownBy(() -> commands.remove(OWNER, parent)).isInstanceOf(ConflictException.class);
    }

    @Test
    void remove_rejectsAnUnknownRole() {
        LifeRoleId unknown = LifeRoleId.random();

        assertThatThrownBy(() -> commands.remove(OWNER, unknown)).isInstanceOf(NotFoundException.class);
    }

    private LifeRoleId add(String name) {
        LifeRoleId id = LifeRoleId.random();
        commands.add(new AddLifeRole(id, OWNER, name, ""));
        return id;
    }
}
