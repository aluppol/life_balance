package com.luppol.lifebalance.adapter.persistence.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LifeRoleJpaRepository extends JpaRepository<LifeRoleEntity, UUID> {
    List<LifeRoleEntity> findAllByPersonIdOrderByPositionAscNameAsc(String personId);

    Optional<LifeRoleEntity> findByPersonIdAndId(String personId, UUID id);

    @Query("select count(goal) > 0 from GoalEntity goal where goal.personId = :personId and goal.lifeRoleId = :id")
    boolean hasGoals(String personId, UUID id);

    @Query("""
            select count(activity) > 0 from PlannedActivityEntity activity
            where activity.personId = :personId and activity.lifeRoleId = :id
            """)
    boolean hasActivities(String personId, UUID id);

    @Modifying
    @Query("delete from LifeRoleEntity role where role.personId = :personId and role.id = :id")
    void deleteOwned(String personId, UUID id);
}
