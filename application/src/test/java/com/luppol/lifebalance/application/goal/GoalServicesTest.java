package com.luppol.lifebalance.application.goal;

import com.luppol.lifebalance.application.fakes.PlannerStore;
import com.luppol.lifebalance.domain.NotFoundException;
import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalDetails;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.goal.GoalStatus;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GoalServicesTest {
    private static final PersonId OWNER = new PersonId("owner");
    private static final PersonId STRANGER = new PersonId("stranger");

    private final PlannerStore store = new PlannerStore();
    private final GoalCommands commands = new GoalCommandService(store.goals(), store.lifeRoles(), store.coreValues());
    private final GoalQueries queries = new GoalQueryService(store.goals());
    private final LifeRole parent = LifeRole.personal(LifeRoleId.random(), OWNER, "Parent", "", 1);
    private final CoreValue family = new CoreValue(CoreValueId.random(), OWNER, "Family", "", 0);

    GoalServicesTest() {
        store.lifeRoles().add(parent);
        store.coreValues().add(family);
    }

    @Test
    void set_storesAnActiveGoal() {
        GoalId id = set(details("Teach cycling"));

        assertThat(queries.find(OWNER, id)).isEqualTo(new Goal(id, OWNER, details("Teach cycling"), GoalStatus.ACTIVE));
        assertThat(queries.listAll(OWNER)).hasSize(1);
    }

    @Test
    void set_rejectsARoleOfAnotherPerson() {
        LifeRole foreign = LifeRole.personal(LifeRoleId.random(), STRANGER, "Parent", "", 0);
        store.lifeRoles().add(foreign);
        GoalDetails details = new GoalDetails(foreign.id(), "Mine", "", Optional.empty(), Set.of());

        assertThatThrownBy(() -> set(details))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("Life role %s does not exist".formatted(foreign.id()));
    }

    @Test
    void set_rejectsUnknownValues() {
        CoreValueId unknown = CoreValueId.random();
        GoalDetails details = new GoalDetails(parent.id(), "Mine", "", Optional.empty(), Set.of(family.id(), unknown));

        assertThatThrownBy(() -> set(details))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("Core values [%s] do not exist".formatted(unknown));
    }

    @Test
    void revise_replacesTheDetails() {
        GoalId id = set(details("Teach cycling"));

        commands.revise(new ReviseGoal(id, OWNER, details("Teach swimming")));

        assertThat(queries.find(OWNER, id).details().title()).isEqualTo("Teach swimming");
    }

    @Test
    void revise_checksReferences() {
        GoalId id = set(details("Teach cycling"));
        GoalDetails unknownRole = new GoalDetails(LifeRoleId.random(), "x", "", Optional.empty(), Set.of());

        assertThatThrownBy(() -> commands.revise(new ReviseGoal(id, OWNER, unknownRole)))
                .isInstanceOf(RuleViolationException.class);
    }

    @Test
    void revise_rejectsAGoalOfAnotherPerson() {
        GoalId id = set(details("Teach cycling"));

        assertThatThrownBy(() -> commands.revise(new ReviseGoal(id, STRANGER, details("x"))))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void statusCommands_moveTheGoalThroughItsLife() {
        GoalId id = set(details("Teach cycling"));

        commands.achieve(OWNER, id);
        assertThat(queries.find(OWNER, id).status()).isEqualTo(GoalStatus.ACHIEVED);

        commands.reopen(OWNER, id);
        assertThat(queries.find(OWNER, id).status()).isEqualTo(GoalStatus.ACTIVE);

        commands.drop(OWNER, id);
        assertThat(queries.find(OWNER, id).status()).isEqualTo(GoalStatus.DROPPED);
    }

    @Test
    void remove_deletesTheGoal() {
        GoalId id = set(details("Teach cycling"));

        commands.remove(OWNER, id);

        assertThat(queries.listAll(OWNER)).isEmpty();
    }

    @Test
    void remove_rejectsAnUnknownGoal() {
        GoalId unknown = GoalId.random();

        assertThatThrownBy(() -> commands.remove(OWNER, unknown)).isInstanceOf(NotFoundException.class);
    }

    private GoalId set(GoalDetails details) {
        GoalId id = GoalId.random();
        commands.set(new SetGoal(id, OWNER, details));
        return id;
    }

    private GoalDetails details(String title) {
        return new GoalDetails(parent.id(), title, "", Optional.of(LocalDate.of(2026, 12, 1)), Set.of(family.id()));
    }
}
