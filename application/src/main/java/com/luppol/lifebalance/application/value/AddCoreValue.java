package com.luppol.lifebalance.application.value;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.value.CoreValueId;

public record AddCoreValue(CoreValueId id, PersonId owner, String name, String description) {
}
