package com.luppol.lifebalance.domain.role;

import com.luppol.lifebalance.domain.person.PersonId;

import static com.luppol.lifebalance.domain.Invariants.requireMaximumLength;
import static com.luppol.lifebalance.domain.Invariants.requireNotNegative;
import static com.luppol.lifebalance.domain.Invariants.requirePresent;
import static com.luppol.lifebalance.domain.Invariants.requireText;

public record LifeRole(LifeRoleId id, PersonId owner, String name, String description, LifeRoleKind kind, int position) {
    public static final int MAXIMUM_NAME_LENGTH = 100;
    public static final int MAXIMUM_DESCRIPTION_LENGTH = 1000;
    public static final String SHARPEN_THE_SAW_NAME = "Sharpen the Saw";
    public static final String SHARPEN_THE_SAW_DESCRIPTION =
            "Renew the four dimensions of your nature: physical, mental, social/emotional and spiritual.";

    public LifeRole {
        requirePresent(id, "Life role id");
        requirePresent(owner, "Life role owner");
        requireText(name, "Life role name", MAXIMUM_NAME_LENGTH);
        requireMaximumLength(description, "Life role description", MAXIMUM_DESCRIPTION_LENGTH);
        requirePresent(kind, "Life role kind");
        requireNotNegative(position, "Life role position");
    }

    public static LifeRole personal(LifeRoleId id, PersonId owner, String name, String description, int position) {
        return new LifeRole(id, owner, name, description, LifeRoleKind.PERSONAL, position);
    }

    public static LifeRole sharpenTheSaw(PersonId owner) {
        return new LifeRole(LifeRoleId.random(), owner, SHARPEN_THE_SAW_NAME, SHARPEN_THE_SAW_DESCRIPTION,
                LifeRoleKind.SHARPEN_THE_SAW, 0);
    }

    public boolean isBuiltIn() {
        return kind == LifeRoleKind.SHARPEN_THE_SAW;
    }

    public LifeRole revisedTo(String newName, String newDescription) {
        return new LifeRole(id, owner, newName, newDescription, kind, position);
    }

    public LifeRole movedTo(int newPosition) {
        return new LifeRole(id, owner, name, description, kind, newPosition);
    }

    public boolean isNamed(String candidate) {
        return name.equalsIgnoreCase(candidate);
    }
}
