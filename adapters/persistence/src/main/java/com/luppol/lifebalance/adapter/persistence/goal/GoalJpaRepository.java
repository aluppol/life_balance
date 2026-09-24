package com.luppol.lifebalance.adapter.persistence.goal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalJpaRepository extends JpaRepository<GoalEntity, UUID> {
    List<GoalEntity> findAllByPersonIdOrderByCreatedAtAscTitleAsc(String personId);

    Optional<GoalEntity> findByPersonIdAndId(String personId, UUID id);
}
