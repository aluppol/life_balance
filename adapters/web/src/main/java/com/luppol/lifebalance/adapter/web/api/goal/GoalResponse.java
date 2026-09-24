package com.luppol.lifebalance.adapter.web.api.goal;

import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalDetails;
import com.luppol.lifebalance.domain.goal.GoalStatus;
import com.luppol.lifebalance.domain.value.CoreValueId;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record GoalResponse(UUID id, UUID roleId, String title, String description, LocalDate dueOn, GoalStatus status,
                           List<UUID> valueIds) {
    static GoalResponse from(Goal goal) {
        GoalDetails details = goal.details();
        List<UUID> valueIds = details.valueIds().stream().map(CoreValueId::value).sorted().toList();
        return new GoalResponse(goal.id().value(), details.roleId().value(), details.title(), details.description(),
                details.dueOn().orElse(null), goal.status(), valueIds);
    }
}
