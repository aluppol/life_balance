package com.luppol.lifebalance.adapter.web.api.planning;

import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.planning.ActivityDetails;
import com.luppol.lifebalance.domain.planning.Quadrant;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record ActivityRequest(
        @NotNull UUID roleId,
        UUID goalId,
        @NotBlank @Size(max = ActivityDetails.MAXIMUM_TITLE_LENGTH) String title,
        @NotNull Quadrant quadrant,
        LocalDate scheduledOn) {

    ActivityDetails toDetails() {
        return new ActivityDetails(new LifeRoleId(roleId), Optional.ofNullable(goalId).map(GoalId::new), title,
                quadrant, Optional.ofNullable(scheduledOn));
    }
}
