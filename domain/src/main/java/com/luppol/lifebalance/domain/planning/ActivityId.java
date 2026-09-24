package com.luppol.lifebalance.domain.planning;

import java.util.UUID;

import static com.luppol.lifebalance.domain.Invariants.requirePresent;

public record ActivityId(UUID value) {
    public ActivityId {
        requirePresent(value, "Activity id");
    }

    public static ActivityId random() {
        return new ActivityId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
