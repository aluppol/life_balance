package com.luppol.lifebalance.domain.goal;

import com.luppol.lifebalance.domain.NotFoundException;
import com.luppol.lifebalance.domain.person.PersonId;

import java.util.List;
import java.util.Optional;

public interface GoalRepository {
    List<Goal> findAllByOwner(PersonId owner);

    Optional<Goal> findById(PersonId owner, GoalId id);

    default Goal findRequired(PersonId owner, GoalId id) {
        return findById(owner, id).orElseThrow(() -> new NotFoundException("Goal", id));
    }

    void add(Goal goal);

    void update(Goal goal);

    void remove(PersonId owner, GoalId id);
}
