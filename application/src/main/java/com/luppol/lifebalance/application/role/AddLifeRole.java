package com.luppol.lifebalance.application.role;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRoleId;

public record AddLifeRole(LifeRoleId id, PersonId owner, String name, String description) {
}
