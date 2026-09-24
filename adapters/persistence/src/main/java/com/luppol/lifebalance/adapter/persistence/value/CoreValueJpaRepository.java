package com.luppol.lifebalance.adapter.persistence.value;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoreValueJpaRepository extends JpaRepository<CoreValueEntity, UUID> {
    List<CoreValueEntity> findAllByPersonIdOrderByPositionAscNameAsc(String personId);

    Optional<CoreValueEntity> findByPersonIdAndId(String personId, UUID id);

    @Modifying
    @Query("delete from CoreValueEntity value where value.personId = :personId and value.id = :id")
    void deleteOwned(String personId, UUID id);
}
