package com.luppol.lifebalance.domain.planning;

import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.role.LifeRoleId;

import java.time.LocalDate;
import java.util.Optional;

import static com.luppol.lifebalance.domain.Invariants.requirePresent;
import static com.luppol.lifebalance.domain.Invariants.requireText;

public record ActivityDetails(LifeRoleId roleId, Optional<GoalId> goalId, String title, Quadrant quadrant,
                              Optional<LocalDate> scheduledOn) {
    public static final int MAXIMUM_TITLE_LENGTH = 200;

    public ActivityDetails {
        requirePresent(roleId, "Activity role");
        requirePresent(goalId, "Activity goal");
        requireText(title, "Activity title", MAXIMUM_TITLE_LENGTH);
        requirePresent(quadrant, "Activity quadrant");
        requirePresent(scheduledOn, "Activity date");
    }

    public boolean isBigRock() {
        return quadrant == Quadrant.IMPORTANT_NOT_URGENT;
    }
}
