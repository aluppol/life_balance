package com.luppol.lifebalance.adapter.persistence.goal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record GoalCoreValue(@Column(name = "person_id") String personId,
                            @Column(name = "core_value_id") UUID coreValueId) {
}
