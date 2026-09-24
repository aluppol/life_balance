package com.luppol.lifebalance.adapter.web.api.value;

import com.luppol.lifebalance.domain.value.CoreValue;

import java.util.UUID;

public record CoreValueResponse(UUID id, String name, String description, int position) {
    static CoreValueResponse from(CoreValue value) {
        return new CoreValueResponse(value.id().value(), value.name(), value.description(), value.position());
    }
}
