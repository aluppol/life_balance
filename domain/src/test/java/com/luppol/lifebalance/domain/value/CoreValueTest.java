package com.luppol.lifebalance.domain.value;

import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.person.PersonId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoreValueTest {
    private static final PersonId OWNER = new PersonId("owner");
    private static final CoreValueId ID = new CoreValueId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    private static final CoreValue INTEGRITY = new CoreValue(ID, OWNER, "Integrity", "Keep promises", 2);

    @Test
    void revisedTo_changesNameAndDescriptionOnly() {
        assertThat(INTEGRITY.revisedTo("Honesty", "Tell the truth"))
                .isEqualTo(new CoreValue(ID, OWNER, "Honesty", "Tell the truth", 2));
    }

    @Test
    void movedTo_changesPositionOnly() {
        assertThat(INTEGRITY.movedTo(0)).isEqualTo(new CoreValue(ID, OWNER, "Integrity", "Keep promises", 0));
    }

    @Test
    void isNamed_ignoresCase() {
        assertThat(INTEGRITY.isNamed("INTEGRITY")).isTrue();
    }

    @Test
    void isNamed_isFalseForAnotherName() {
        assertThat(INTEGRITY.isNamed("Courage")).isFalse();
    }

    @Test
    void value_requiresAName() {
        assertThatThrownBy(() -> new CoreValue(ID, OWNER, "", "", 0)).isInstanceOf(RuleViolationException.class);
    }

    @Test
    void value_rejectsANameLongerThan100Characters() {
        assertThatThrownBy(() -> new CoreValue(ID, OWNER, "n".repeat(101), "", 0))
                .hasMessage("Core value name must be at most 100 characters");
    }

    @Test
    void value_rejectsADescriptionLongerThan1000Characters() {
        assertThatThrownBy(() -> new CoreValue(ID, OWNER, "Integrity", "d".repeat(1001), 0))
                .hasMessage("Core value description must be at most 1000 characters");
    }

    @Test
    void value_rejectsANegativePosition() {
        assertThatThrownBy(() -> new CoreValue(ID, OWNER, "Integrity", "", -1))
                .hasMessage("Core value position must not be negative");
    }

    @Test
    void value_requiresAnIdAndAnOwner() {
        assertThatThrownBy(() -> new CoreValue(null, OWNER, "Integrity", "", 0)).hasMessage("Core value id is required");
        assertThatThrownBy(() -> new CoreValue(ID, null, "Integrity", "", 0)).hasMessage("Core value owner is required");
    }

    @Test
    void id_isRandomAndPrintable() {
        CoreValueId random = CoreValueId.random();
        assertThat(random).isNotEqualTo(CoreValueId.random()).hasToString(random.value().toString());
    }

    @Test
    void id_requiresAValue() {
        assertThatThrownBy(() -> new CoreValueId(null)).isInstanceOf(RuleViolationException.class);
    }
}
