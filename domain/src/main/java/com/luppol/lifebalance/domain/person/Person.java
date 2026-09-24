package com.luppol.lifebalance.domain.person;

import static com.luppol.lifebalance.domain.Invariants.requirePresent;

public record Person(PersonId id, PersonKind kind) {
    public Person {
        requirePresent(id, "Person id");
        requirePresent(kind, "Person kind");
    }

    public static Person member(PersonId id) {
        return new Person(id, PersonKind.MEMBER);
    }

    public static Person guest(PersonId id) {
        return new Person(id, PersonKind.GUEST);
    }
}
