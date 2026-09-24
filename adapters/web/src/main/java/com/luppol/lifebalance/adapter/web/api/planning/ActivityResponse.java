package com.luppol.lifebalance.adapter.web.api.planning;

import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.planning.ActivityDetails;
import com.luppol.lifebalance.domain.planning.PlannedActivity;
import com.luppol.lifebalance.domain.planning.Quadrant;

import java.time.LocalDate;
import java.util.UUID;

public record ActivityResponse(UUID id, LocalDate weekStart, UUID roleId, UUID goalId, String title, Quadrant quadrant,
                               LocalDate scheduledOn, boolean isCompleted) {
    static ActivityResponse from(PlannedActivity activity) {
        ActivityDetails details = activity.details();
        return new ActivityResponse(activity.id().value(), activity.week().monday(), details.roleId().value(),
                details.goalId().map(GoalId::value).orElse(null), details.title(), details.quadrant(),
                details.scheduledOn().orElse(null), activity.isCompleted());
    }
}
