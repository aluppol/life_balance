package com.luppol.lifebalance.application.value;

import com.luppol.lifebalance.application.fakes.PlannerStore;
import com.luppol.lifebalance.domain.ConflictException;
import com.luppol.lifebalance.domain.NotFoundException;
import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoreValueServicesTest {
    private static final PersonId OWNER = new PersonId("owner");
    private static final PersonId STRANGER = new PersonId("stranger");

    private final PlannerStore store = new PlannerStore();
    private final CoreValueCommands commands = new CoreValueCommandService(store.coreValues());
    private final CoreValueQueries queries = new CoreValueQueryService(store.coreValues());

    @Test
    void add_appendsAfterTheLastValue() {
        CoreValueId first = add("Integrity");
        CoreValueId second = add("Family");

        assertThat(queries.listAll(OWNER)).extracting(CoreValue::id).containsExactly(first, second);
        assertThat(queries.find(OWNER, second).position()).isEqualTo(1);
    }

    @Test
    void add_rejectsANameTheOwnerAlreadyUses() {
        add("Integrity");

        assertThatThrownBy(() -> add("integrity"))
                .isInstanceOf(ConflictException.class)
                .hasMessage("A core value named 'integrity' already exists");
    }

    @Test
    void add_allowsANameAnotherPersonUses() {
        commands.add(new AddCoreValue(CoreValueId.random(), STRANGER, "Integrity", ""));

        add("Integrity");

        assertThat(queries.listAll(OWNER)).hasSize(1);
    }

    @Test
    void revise_changesNameAndDescription() {
        CoreValueId id = add("Integrity");

        commands.revise(new ReviseCoreValue(id, OWNER, "INTEGRITY", "Say what you do"));

        assertThat(queries.find(OWNER, id)).isEqualTo(new CoreValue(id, OWNER, "INTEGRITY", "Say what you do", 0));
    }

    @Test
    void revise_rejectsTheNameOfAnotherValue() {
        add("Integrity");
        CoreValueId family = add("Family");

        assertThatThrownBy(() -> commands.revise(new ReviseCoreValue(family, OWNER, "Integrity", "")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void revise_rejectsAValueOfAnotherPerson() {
        CoreValueId id = add("Integrity");

        assertThatThrownBy(() -> commands.revise(new ReviseCoreValue(id, STRANGER, "Mine now", "")))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Core value %s not found".formatted(id));
    }

    @Test
    void reorder_rewritesPositions() {
        CoreValueId integrity = add("Integrity");
        CoreValueId family = add("Family");
        CoreValueId growth = add("Growth");

        commands.reorder(new ReorderCoreValues(OWNER, List.of(growth, integrity, family)));

        assertThat(queries.listAll(OWNER)).extracting(CoreValue::id).containsExactly(growth, integrity, family);
        assertThat(queries.listAll(OWNER)).extracting(CoreValue::position).containsExactly(0, 1, 2);
    }

    @Test
    void reorder_rejectsAnIncompleteOrder() {
        CoreValueId integrity = add("Integrity");
        add("Family");

        assertThatThrownBy(() -> commands.reorder(new ReorderCoreValues(OWNER, List.of(integrity))))
                .isInstanceOf(RuleViolationException.class);
    }

    @Test
    void remove_deletesTheValue() {
        CoreValueId id = add("Integrity");

        commands.remove(OWNER, id);

        assertThat(queries.listAll(OWNER)).isEmpty();
    }

    @Test
    void remove_rejectsAnUnknownValue() {
        CoreValueId unknown = CoreValueId.random();

        assertThatThrownBy(() -> commands.remove(OWNER, unknown)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void find_rejectsAnUnknownValue() {
        CoreValueId unknown = CoreValueId.random();

        assertThatThrownBy(() -> queries.find(OWNER, unknown)).isInstanceOf(NotFoundException.class);
    }


    @Test
    void add_rejectsTheThirtyFirstValue() {
        for (int index = 0; index < CoreValue.MAXIMUM_PER_PERSON; index++) {
            add("Value " + index);
        }

        assertThatThrownBy(() -> add("One too many"))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("A person can keep at most 30 core values");
    }

    private CoreValueId add(String name) {
        CoreValueId id = CoreValueId.random();
        commands.add(new AddCoreValue(id, OWNER, name, "About " + name));
        return id;
    }
}
