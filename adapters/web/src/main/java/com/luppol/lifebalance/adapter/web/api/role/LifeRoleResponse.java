package com.luppol.lifebalance.adapter.web.api.role;

import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleKind;

import java.util.UUID;

public record LifeRoleResponse(UUID id, String name, String description, LifeRoleKind kind, int position) {
    static LifeRoleResponse from(LifeRole role) {
        return new LifeRoleResponse(role.id().value(), role.name(), role.description(), role.kind(), role.position());
    }
}
