package com.luppol.lifebalance.application.role;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRoleId;

public interface LifeRoleCommands {
    void add(AddLifeRole command);

    void revise(ReviseLifeRole command);

    void reorder(ReorderLifeRoles command);

    void remove(PersonId owner, LifeRoleId id);
}
