package com.luppol.lifebalance.application.planning;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.ActivityDetails;
import com.luppol.lifebalance.domain.planning.ActivityId;
import com.luppol.lifebalance.domain.planning.WeekStart;

public record PlanActivity(ActivityId id, PersonId owner, WeekStart week, ActivityDetails details) {
}
