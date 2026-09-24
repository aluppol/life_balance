package com.luppol.lifebalance.adapter.web.api.planning;

import com.luppol.lifebalance.domain.planning.Tally;
import com.luppol.lifebalance.domain.role.LifeRoleId;

import java.util.Map;
import java.util.UUID;

public record RoleTallyResponse(UUID roleId, int planned, int completed) {
    static RoleTallyResponse from(Map.Entry<LifeRoleId, Tally> roleTally) {
        Tally tally = roleTally.getValue();
        return new RoleTallyResponse(roleTally.getKey().value(), tally.planned(), tally.completed());
    }
}
