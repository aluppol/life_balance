package com.luppol.lifebalance.adapter.persistence.value;

import com.luppol.lifebalance.adapter.persistence.PersistenceTest;
import com.luppol.lifebalance.domain.ConflictException;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoreValueRepositoryAdapterTest extends PersistenceTest {
    private final CoreValue integrity = new CoreValue(CoreValueId.random(), OWNER, "Integrity", "Keep promises", 1);
    private final CoreValue family = new CoreValue(CoreValueId.random(), OWNER, "Family", "", 0);

    @BeforeEach
    void enrollPeople() {
        enroll(OWNER, STRANGER);
    }

    @Test
    void findAllByOwner_ordersByPosition() {
        coreValues.add(integrity);
        coreValues.add(family);
        flushAndClear();

        assertThat(coreValues.findAllByOwner(OWNER)).containsExactly(family, integrity);
    }

    @Test
    void findById_isScopedToTheOwner() {
        coreValues.add(integrity);
        flushAndClear();

        assertThat(coreValues.findById(OWNER, integrity.id())).contains(integrity);
        assertThat(coreValues.findById(STRANGER, integrity.id())).isEmpty();
    }

    @Test
    void add_rejectsADuplicateNameIgnoringCase() {
        coreValues.add(integrity);
        CoreValue duplicate = new CoreValue(CoreValueId.random(), OWNER, "INTEGRITY", "", 2);

        assertThatThrownBy(() -> coreValues.add(duplicate))
                .isInstanceOf(ConflictException.class)
                .hasMessage("A core value named 'INTEGRITY' already exists");
    }

    @Test
    void update_andUpdateAll_changeStoredValues() {
        coreValues.add(integrity);
        coreValues.add(family);
        flushAndClear();

        coreValues.update(integrity.revisedTo("Honesty", "Tell the truth"));
        coreValues.updateAll(List.of(family.movedTo(5)));
        flushAndClear();

        assertThat(coreValues.findById(OWNER, integrity.id())).contains(integrity.revisedTo("Honesty", "Tell the truth"));
        assertThat(coreValues.findById(OWNER, family.id())).contains(family.movedTo(5));
    }

    @Test
    void update_rejectsTheNameOfAnotherValue() {
        coreValues.add(integrity);
        coreValues.add(family);
        flushAndClear();

        assertThatThrownBy(() -> coreValues.update(family.revisedTo("Integrity", "")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void remove_onlyDeletesTheOwnersValue() {
        coreValues.add(integrity);
        flushAndClear();

        coreValues.remove(STRANGER, integrity.id());
        assertThat(coreValues.findById(OWNER, integrity.id())).isPresent();

        coreValues.remove(OWNER, integrity.id());
        assertThat(coreValues.findById(OWNER, integrity.id())).isEmpty();
    }
}
