package com.luppol.lifebalance.adapter.persistence.planning;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlannedActivityJpaRepository extends JpaRepository<PlannedActivityEntity, UUID> {
    @Query("""
            select activity from PlannedActivityEntity activity
            where activity.personId = :personId and activity.weekStart = :weekStart
            order by activity.scheduledOn asc nulls last, activity.createdAt asc, activity.title asc
            """)
    List<PlannedActivityEntity> findAllInWeek(String personId, LocalDate weekStart);

    Optional<PlannedActivityEntity> findByPersonIdAndId(String personId, UUID id);

    @Modifying
    @Query("delete from PlannedActivityEntity activity where activity.personId = :personId and activity.id = :id")
    void deleteOwned(String personId, UUID id);
}
