package com.luppol.lifebalance.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderingTest {
    @Test
    void nextPosition_isZero_whenNothingIsOrdered() {
        assertThat(Ordering.nextPosition(List.<Integer>of(), Integer::intValue)).isZero();
    }

    @Test
    void nextPosition_followsTheHighestPosition() {
        assertThat(Ordering.nextPosition(List.of(3, 0, 7), Integer::intValue)).isEqualTo(8);
    }

    @Test
    void requirePermutation_acceptsTheSameItemsInAnotherOrder() {
        assertThatCode(() -> Ordering.requirePermutation(List.of("b", "c", "a"), List.of("a", "b", "c")))
                .doesNotThrowAnyException();
    }

    @Test
    void requirePermutation_rejectsAMissingItem() {
        assertThatThrownBy(() -> Ordering.requirePermutation(List.of("a", "b"), List.of("a", "b", "c")))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("The new order must list every item exactly once");
    }

    @Test
    void requirePermutation_rejectsADuplicatedItem() {
        assertThatThrownBy(() -> Ordering.requirePermutation(List.of("a", "a", "b"), List.of("a", "b", "c")))
                .isInstanceOf(RuleViolationException.class);
    }

    @Test
    void requirePermutation_rejectsAForeignItem() {
        assertThatThrownBy(() -> Ordering.requirePermutation(List.of("a", "b", "x"), List.of("a", "b", "c")))
                .isInstanceOf(RuleViolationException.class);
    }
}
