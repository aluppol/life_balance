package com.luppol.lifebalance.adapter.persistence.planning;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.ActivityId;
import com.luppol.lifebalance.domain.planning.PlannedActivity;
import com.luppol.lifebalance.domain.planning.PlannedActivityRepository;
import com.luppol.lifebalance.domain.planning.WeekStart;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PlannedActivityRepositoryAdapter implements PlannedActivityRepository {
    private final PlannedActivityJpaRepository activities;
    private final EntityManager entityManager;

    public PlannedActivityRepositoryAdapter(PlannedActivityJpaRepository activities, EntityManager entityManager) {
        this.activities = activities;
        this.entityManager = entityManager;
    }

    @Override
    public List<PlannedActivity> findAllInWeek(PersonId owner, WeekStart week) {
        return activities.findAllInWeek(owner.value(), week.monday()).stream()
                .map(PlannedActivityEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<PlannedActivity> findById(PersonId owner, ActivityId id) {
        return activities.findByPersonIdAndId(owner.value(), id.value()).map(PlannedActivityEntity::toDomain);
    }

    @Override
    public void add(PlannedActivity activity) {
        entityManager.persist(PlannedActivityEntity.from(activity));
    }

    @Override
    public void update(PlannedActivity activity) {
        entityManager.merge(PlannedActivityEntity.from(activity));
    }

    @Override
    public void remove(PersonId owner, ActivityId id) {
        activities.deleteOwned(owner.value(), id.value());
    }
}
