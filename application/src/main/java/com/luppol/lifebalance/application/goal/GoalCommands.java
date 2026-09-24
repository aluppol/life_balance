package com.luppol.lifebalance.application.goal;

import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.person.PersonId;

public interface GoalCommands {
    void set(SetGoal command);

    void revise(ReviseGoal command);

    void achieve(PersonId owner, GoalId id);

    void drop(PersonId owner, GoalId id);

    void reopen(PersonId owner, GoalId id);

    void remove(PersonId owner, GoalId id);
}
