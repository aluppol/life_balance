package com.luppol.lifebalance.domain.goal;

import java.util.UUID;

import static com.luppol.lifebalance.domain.Invariants.requirePresent;

public record GoalId(UUID value) {
    public GoalId {
        requirePresent(value, "Goal id");
    }

    public static GoalId random() {
        return new GoalId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
