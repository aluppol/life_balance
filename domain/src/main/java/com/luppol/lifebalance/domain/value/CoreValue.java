package com.luppol.lifebalance.domain.value;

import com.luppol.lifebalance.domain.person.PersonId;

import static com.luppol.lifebalance.domain.Invariants.requireMaximumLength;
import static com.luppol.lifebalance.domain.Invariants.requireNotNegative;
import static com.luppol.lifebalance.domain.Invariants.requirePresent;
import static com.luppol.lifebalance.domain.Invariants.requireText;

public record CoreValue(CoreValueId id, PersonId owner, String name, String description, int position) {
    public static final int MAXIMUM_NAME_LENGTH = 100;
    public static final int MAXIMUM_DESCRIPTION_LENGTH = 1000;

    public CoreValue {
        requirePresent(id, "Core value id");
        requirePresent(owner, "Core value owner");
        requireText(name, "Core value name", MAXIMUM_NAME_LENGTH);
        requireMaximumLength(description, "Core value description", MAXIMUM_DESCRIPTION_LENGTH);
        requireNotNegative(position, "Core value position");
    }

    public CoreValue revisedTo(String newName, String newDescription) {
        return new CoreValue(id, owner, newName, newDescription, position);
    }

    public CoreValue movedTo(int newPosition) {
        return new CoreValue(id, owner, name, description, newPosition);
    }

    public boolean isNamed(String candidate) {
        return name.equalsIgnoreCase(candidate);
    }
}
