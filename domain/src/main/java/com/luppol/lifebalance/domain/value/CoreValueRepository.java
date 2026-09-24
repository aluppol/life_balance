package com.luppol.lifebalance.domain.value;

import com.luppol.lifebalance.domain.NotFoundException;
import com.luppol.lifebalance.domain.person.PersonId;

import java.util.List;
import java.util.Optional;

public interface CoreValueRepository {
    List<CoreValue> findAllByOwner(PersonId owner);

    Optional<CoreValue> findById(PersonId owner, CoreValueId id);

    default CoreValue findRequired(PersonId owner, CoreValueId id) {
        return findById(owner, id).orElseThrow(() -> new NotFoundException("Core value", id));
    }

    void add(CoreValue value);

    void update(CoreValue value);

    void updateAll(List<CoreValue> values);

    void remove(PersonId owner, CoreValueId id);
}
