package com.luppol.lifebalance.application.planning;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.ActivityId;
import com.luppol.lifebalance.domain.planning.PlannedActivity;
import com.luppol.lifebalance.domain.planning.PlannedActivityRepository;
import com.luppol.lifebalance.domain.planning.WeekScorecard;
import com.luppol.lifebalance.domain.planning.WeekStart;

import java.util.List;

public class PlanningQueryService implements PlanningQueries {
    private final PlannedActivityRepository activities;

    public PlanningQueryService(PlannedActivityRepository activities) {
        this.activities = activities;
    }

    @Override
    public List<PlannedActivity> listWeek(PersonId owner, WeekStart week) {
        return activities.findAllInWeek(owner, week);
    }

    @Override
    public PlannedActivity find(PersonId owner, ActivityId id) {
        return activities.findRequired(owner, id);
    }

    @Override
    public WeekScorecard scorecard(PersonId owner, WeekStart week) {
        return WeekScorecard.of(week, activities.findAllInWeek(owner, week));
    }
}
