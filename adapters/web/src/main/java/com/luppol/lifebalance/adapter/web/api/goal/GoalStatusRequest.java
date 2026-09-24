package com.luppol.lifebalance.adapter.web.api.goal;

import com.luppol.lifebalance.domain.goal.GoalStatus;
import jakarta.validation.constraints.NotNull;

public record GoalStatusRequest(@NotNull GoalStatus status) {
}
