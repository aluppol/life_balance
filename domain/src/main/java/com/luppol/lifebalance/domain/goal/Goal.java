package com.luppol.lifebalance.domain.goal;

import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.person.PersonId;

import static com.luppol.lifebalance.domain.Invariants.requirePresent;

public record Goal(GoalId id, PersonId owner, GoalDetails details, GoalStatus status) {
    public static final int MAXIMUM_PER_PERSON = 200;

    public Goal {
        requirePresent(id, "Goal id");
        requirePresent(owner, "Goal owner");
        requirePresent(details, "Goal details");
        requirePresent(status, "Goal status");
    }

    public static Goal set(GoalId id, PersonId owner, GoalDetails details) {
        return new Goal(id, owner, details, GoalStatus.ACTIVE);
    }

    public boolean isActive() {
        return status == GoalStatus.ACTIVE;
    }

    public Goal revisedTo(GoalDetails newDetails) {
        return new Goal(id, owner, newDetails, status);
    }

    public Goal achieved() {
        requireActive("Only an active goal can be achieved");
        return new Goal(id, owner, details, GoalStatus.ACHIEVED);
    }

    public Goal dropped() {
        requireActive("Only an active goal can be dropped");
        return new Goal(id, owner, details, GoalStatus.DROPPED);
    }

    public Goal reopened() {
        if (isActive()) {
            throw new RuleViolationException("Only an achieved or dropped goal can be reopened");
        }
        return new Goal(id, owner, details, GoalStatus.ACTIVE);
    }

    private void requireActive(String message) {
        if (!isActive()) {
            throw new RuleViolationException(message);
        }
    }
}
