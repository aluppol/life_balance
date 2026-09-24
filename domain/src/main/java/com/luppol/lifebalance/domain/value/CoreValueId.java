package com.luppol.lifebalance.domain.value;

import java.util.UUID;

import static com.luppol.lifebalance.domain.Invariants.requirePresent;

public record CoreValueId(UUID value) {
    public CoreValueId {
        requirePresent(value, "Core value id");
    }

    public static CoreValueId random() {
        return new CoreValueId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
