package com.luppol.lifebalance.domain.person;

import java.time.Instant;

import static com.luppol.lifebalance.domain.Invariants.requirePresent;

public record MemberActivity(PersonId id, Instant lastActiveAt) {
    public MemberActivity {
        requirePresent(id, "Member id");
        requirePresent(lastActiveAt, "Last active time");
    }
}
