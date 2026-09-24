package com.luppol.lifebalance.application.role;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.role.LifeRoleRepository;

import java.util.List;

public class LifeRoleQueryService implements LifeRoleQueries {
    private final LifeRoleRepository lifeRoles;

    public LifeRoleQueryService(LifeRoleRepository lifeRoles) {
        this.lifeRoles = lifeRoles;
    }

    @Override
    public List<LifeRole> listAll(PersonId owner) {
        return lifeRoles.findAllByOwner(owner);
    }

    @Override
    public LifeRole find(PersonId owner, LifeRoleId id) {
        return lifeRoles.findRequired(owner, id);
    }
}
