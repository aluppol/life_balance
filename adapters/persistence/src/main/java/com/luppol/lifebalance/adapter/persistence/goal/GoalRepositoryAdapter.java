package com.luppol.lifebalance.adapter.persistence.goal;

import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.goal.GoalRepository;
import com.luppol.lifebalance.domain.person.PersonId;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GoalRepositoryAdapter implements GoalRepository {
    private final GoalJpaRepository goals;
    private final EntityManager entityManager;

    public GoalRepositoryAdapter(GoalJpaRepository goals, EntityManager entityManager) {
        this.goals = goals;
        this.entityManager = entityManager;
    }

    @Override
    public List<Goal> findAllByOwner(PersonId owner) {
        return goals.findAllByPersonIdOrderByCreatedAtAscTitleAsc(owner.value()).stream()
                .map(GoalEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Goal> findById(PersonId owner, GoalId id) {
        return goals.findByPersonIdAndId(owner.value(), id.value()).map(GoalEntity::toDomain);
    }

    @Override
    public void add(Goal goal) {
        entityManager.persist(GoalEntity.from(goal));
    }

    @Override
    public void update(Goal goal) {
        entityManager.merge(GoalEntity.from(goal));
    }

    @Override
    public void remove(PersonId owner, GoalId id) {
        goals.findByPersonIdAndId(owner.value(), id.value()).ifPresent(entityManager::remove);
    }
}
