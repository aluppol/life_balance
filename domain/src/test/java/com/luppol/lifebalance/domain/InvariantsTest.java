package com.luppol.lifebalance.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvariantsTest {
    @Test
    void requirePresent_rejectsNull() {
        assertThatThrownBy(() -> Invariants.requirePresent(null, "Thing"))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("Thing is required");
    }

    @Test
    void requirePresent_acceptsValue() {
        assertThatCode(() -> Invariants.requirePresent("value", "Thing")).doesNotThrowAnyException();
    }

    @Test
    void requireText_rejectsBlank() {
        assertThatThrownBy(() -> Invariants.requireText("  ", "Name", 5))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("Name must not be blank");
    }

    @Test
    void requireText_rejectsNull() {
        assertThatThrownBy(() -> Invariants.requireText(null, "Name", 5))
                .hasMessage("Name is required");
    }

    @Test
    void requireText_acceptsTextOfMaximumLength() {
        assertThatCode(() -> Invariants.requireText("abcde", "Name", 5)).doesNotThrowAnyException();
    }

    @Test
    void requireText_rejectsTextLongerThanMaximum() {
        assertThatThrownBy(() -> Invariants.requireText("abcdef", "Name", 5))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("Name must be at most 5 characters");
    }

    @Test
    void requireMaximumLength_acceptsEmptyText() {
        assertThatCode(() -> Invariants.requireMaximumLength("", "Description", 5)).doesNotThrowAnyException();
    }

    @Test
    void requireMaximumLength_rejectsNull() {
        assertThatThrownBy(() -> Invariants.requireMaximumLength(null, "Description", 5))
                .hasMessage("Description is required");
    }

    @Test
    void requireNotNegative_acceptsZero() {
        assertThatCode(() -> Invariants.requireNotNegative(0, "Position")).doesNotThrowAnyException();
    }

    @Test
    void requireNotNegative_rejectsMinusOne() {
        assertThatThrownBy(() -> Invariants.requireNotNegative(-1, "Position"))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("Position must not be negative");
    }
}
