package com.luppol.lifebalance.application.role;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRoleId;

import java.util.List;

public record ReorderLifeRoles(PersonId owner, List<LifeRoleId> orderedIds) {
    public ReorderLifeRoles {
        orderedIds = List.copyOf(orderedIds);
    }
}
