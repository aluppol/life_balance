package com.luppol.lifebalance.domain.planning;

import com.luppol.lifebalance.domain.NotFoundException;
import com.luppol.lifebalance.domain.person.PersonId;

import java.util.List;
import java.util.Optional;

public interface PlannedActivityRepository {
    List<PlannedActivity> findAllInWeek(PersonId owner, WeekStart week);

    Optional<PlannedActivity> findById(PersonId owner, ActivityId id);

    default PlannedActivity findRequired(PersonId owner, ActivityId id) {
        return findById(owner, id).orElseThrow(() -> new NotFoundException("Activity", id));
    }

    void add(PlannedActivity activity);

    void update(PlannedActivity activity);

    void remove(PersonId owner, ActivityId id);
}
