package com.luppol.lifebalance.domain.role;

import com.luppol.lifebalance.domain.NotFoundException;
import com.luppol.lifebalance.domain.person.PersonId;

import java.util.List;
import java.util.Optional;

public interface LifeRoleRepository {
    List<LifeRole> findAllByOwner(PersonId owner);

    Optional<LifeRole> findById(PersonId owner, LifeRoleId id);

    default LifeRole findRequired(PersonId owner, LifeRoleId id) {
        return findById(owner, id).orElseThrow(() -> new NotFoundException("Life role", id));
    }

    boolean isInUse(PersonId owner, LifeRoleId id);

    void add(LifeRole role);

    void update(LifeRole role);

    void updateAll(List<LifeRole> roles);

    void remove(PersonId owner, LifeRoleId id);
}
