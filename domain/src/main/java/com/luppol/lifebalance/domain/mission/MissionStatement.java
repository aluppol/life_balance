package com.luppol.lifebalance.domain.mission;

import com.luppol.lifebalance.domain.person.PersonId;

import static com.luppol.lifebalance.domain.Invariants.requirePresent;
import static com.luppol.lifebalance.domain.Invariants.requireText;

public record MissionStatement(PersonId owner, String text) {
    public static final int MAXIMUM_TEXT_LENGTH = 4000;

    public MissionStatement {
        requirePresent(owner, "Mission statement owner");
        requireText(text, "Mission statement", MAXIMUM_TEXT_LENGTH);
    }
}
