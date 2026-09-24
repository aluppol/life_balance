package com.luppol.lifebalance.application.value;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.value.CoreValueId;

import java.util.List;

public record ReorderCoreValues(PersonId owner, List<CoreValueId> orderedIds) {
    public ReorderCoreValues {
        orderedIds = List.copyOf(orderedIds);
    }
}
