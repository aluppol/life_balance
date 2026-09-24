package com.luppol.lifebalance.application.goal;

import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.goal.GoalRepository;
import com.luppol.lifebalance.domain.person.PersonId;

import java.util.List;

public class GoalQueryService implements GoalQueries {
    private final GoalRepository goals;

    public GoalQueryService(GoalRepository goals) {
        this.goals = goals;
    }

    @Override
    public List<Goal> listAll(PersonId owner) {
        return goals.findAllByOwner(owner);
    }

    @Override
    public Goal find(PersonId owner, GoalId id) {
        return goals.findRequired(owner, id);
    }
}
