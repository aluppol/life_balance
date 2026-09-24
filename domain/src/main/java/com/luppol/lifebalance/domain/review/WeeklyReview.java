package com.luppol.lifebalance.domain.review;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.WeekStart;

import java.util.Set;

import static com.luppol.lifebalance.domain.Invariants.requireMaximumLength;
import static com.luppol.lifebalance.domain.Invariants.requirePresent;

public record WeeklyReview(PersonId owner, WeekStart week, String accomplishments, String lessons,
                           Set<RenewalDimension> renewedDimensions) {
    public static final int MAXIMUM_TEXT_LENGTH = 4000;

    public WeeklyReview {
        requirePresent(owner, "Weekly review owner");
        requirePresent(week, "Weekly review week");
        requireMaximumLength(accomplishments, "Accomplishments", MAXIMUM_TEXT_LENGTH);
        requireMaximumLength(lessons, "Lessons", MAXIMUM_TEXT_LENGTH);
        requirePresent(renewedDimensions, "Renewed dimensions");
        renewedDimensions = Set.copyOf(renewedDimensions);
    }
}
