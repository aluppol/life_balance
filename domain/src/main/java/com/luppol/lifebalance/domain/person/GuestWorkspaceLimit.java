package com.luppol.lifebalance.domain.person;

import static com.luppol.lifebalance.domain.Invariants.requirePositive;

public record GuestWorkspaceLimit(int maximum) {
    public GuestWorkspaceLimit {
        requirePositive(maximum, "Guest workspace limit");
    }
}
