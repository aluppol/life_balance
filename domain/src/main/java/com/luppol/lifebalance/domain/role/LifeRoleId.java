package com.luppol.lifebalance.domain.role;

import java.util.UUID;

import static com.luppol.lifebalance.domain.Invariants.requirePresent;

public record LifeRoleId(UUID value) {
    public LifeRoleId {
        requirePresent(value, "Life role id");
    }

    public static LifeRoleId random() {
        return new LifeRoleId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
