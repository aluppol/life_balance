package com.luppol.lifebalance.application.role;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleId;

import java.util.List;

public interface LifeRoleQueries {
    List<LifeRole> listAll(PersonId owner);

    LifeRole find(PersonId owner, LifeRoleId id);
}
