package com.luppol.lifebalance.adapter.web.api.goal;

import com.luppol.lifebalance.domain.goal.GoalDetails;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.value.CoreValueId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record GoalRequest(
        @NotNull UUID roleId,
        @NotBlank @Size(max = GoalDetails.MAXIMUM_TITLE_LENGTH) String title,
        @Size(max = GoalDetails.MAXIMUM_DESCRIPTION_LENGTH) String description,
        LocalDate dueOn,
        List<@NotNull UUID> valueIds) {

    GoalDetails toDetails() {
        Set<CoreValueId> values = Objects.requireNonNullElse(valueIds, List.<UUID>of()).stream()
                .map(CoreValueId::new)
                .collect(Collectors.toSet());
        return new GoalDetails(new LifeRoleId(roleId), title, Objects.requireNonNullElse(description, ""),
                Optional.ofNullable(dueOn), values);
    }
}
