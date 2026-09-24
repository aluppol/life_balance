package com.luppol.lifebalance.application.goal;

import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.person.PersonId;

import java.util.List;

public interface GoalQueries {
    List<Goal> listAll(PersonId owner);

    Goal find(PersonId owner, GoalId id);
}
