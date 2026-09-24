package com.luppol.lifebalance.application.planning;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.ActivityId;

public interface PlanningCommands {
    void plan(PlanActivity command);

    void revise(ReviseActivity command);

    void complete(PersonId owner, ActivityId id);

    void reopen(PersonId owner, ActivityId id);

    void remove(PersonId owner, ActivityId id);
}
