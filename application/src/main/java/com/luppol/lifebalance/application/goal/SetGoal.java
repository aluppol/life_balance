package com.luppol.lifebalance.application.goal;

import com.luppol.lifebalance.domain.goal.GoalDetails;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.person.PersonId;

public record SetGoal(GoalId id, PersonId owner, GoalDetails details) {
}
