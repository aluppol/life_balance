package com.luppol.lifebalance.application.planning;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.ActivityId;
import com.luppol.lifebalance.domain.planning.PlannedActivity;
import com.luppol.lifebalance.domain.planning.WeekScorecard;
import com.luppol.lifebalance.domain.planning.WeekStart;

import java.util.List;

public interface PlanningQueries {
    List<PlannedActivity> listWeek(PersonId owner, WeekStart week);

    PlannedActivity find(PersonId owner, ActivityId id);

    WeekScorecard scorecard(PersonId owner, WeekStart week);
}
