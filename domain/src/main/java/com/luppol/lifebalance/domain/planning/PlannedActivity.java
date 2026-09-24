package com.luppol.lifebalance.domain.planning;

import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.person.PersonId;

import java.time.LocalDate;

import static com.luppol.lifebalance.domain.Invariants.requirePresent;

public record PlannedActivity(ActivityId id, PersonId owner, WeekStart week, ActivityDetails details,
                              boolean isCompleted) {
    public PlannedActivity {
        requirePresent(id, "Activity id");
        requirePresent(owner, "Activity owner");
        requirePresent(week, "Activity week");
        requirePresent(details, "Activity details");
        details.scheduledOn().ifPresent(date -> requireInside(week, date));
    }

    public static PlannedActivity planned(ActivityId id, PersonId owner, WeekStart week, ActivityDetails details) {
        return new PlannedActivity(id, owner, week, details, false);
    }

    public boolean isBigRock() {
        return details.isBigRock();
    }

    public PlannedActivity revisedTo(ActivityDetails newDetails) {
        return new PlannedActivity(id, owner, week, newDetails, isCompleted);
    }

    public PlannedActivity completed() {
        return new PlannedActivity(id, owner, week, details, true);
    }

    public PlannedActivity reopened() {
        return new PlannedActivity(id, owner, week, details, false);
    }

    private static void requireInside(WeekStart week, LocalDate date) {
        if (!week.includes(date)) {
            throw new RuleViolationException("%s is outside the week starting %s".formatted(date, week));
        }
    }
}
