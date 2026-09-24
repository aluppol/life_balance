package com.luppol.lifebalance.domain.goal;

import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.value.CoreValueId;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static com.luppol.lifebalance.domain.Invariants.requireMaximumLength;
import static com.luppol.lifebalance.domain.Invariants.requirePresent;
import static com.luppol.lifebalance.domain.Invariants.requireText;

public record GoalDetails(LifeRoleId roleId, String title, String description, Optional<LocalDate> dueOn,
                          Set<CoreValueId> valueIds) {
    public static final int MAXIMUM_TITLE_LENGTH = 200;
    public static final int MAXIMUM_DESCRIPTION_LENGTH = 2000;

    public GoalDetails {
        requirePresent(roleId, "Goal role");
        requireText(title, "Goal title", MAXIMUM_TITLE_LENGTH);
        requireMaximumLength(description, "Goal description", MAXIMUM_DESCRIPTION_LENGTH);
        requirePresent(dueOn, "Goal due date");
        requirePresent(valueIds, "Goal values");
        valueIds = Set.copyOf(valueIds);
    }
}
