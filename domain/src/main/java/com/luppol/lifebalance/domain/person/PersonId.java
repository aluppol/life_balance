package com.luppol.lifebalance.domain.person;

import static com.luppol.lifebalance.domain.Invariants.requireText;

public record PersonId(String value) {
    private static final int MAXIMUM_LENGTH = 255;

    public PersonId {
        requireText(value, "Person id", MAXIMUM_LENGTH);
    }

    @Override
    public String toString() {
        return value;
    }
}
