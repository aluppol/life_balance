package com.luppol.lifebalance.domain.person;

import static com.luppol.lifebalance.domain.Invariants.requirePositive;

public record InactivityPeriod(int days) {
    public InactivityPeriod {
        requirePositive(days, "Inactivity period");
    }
}
