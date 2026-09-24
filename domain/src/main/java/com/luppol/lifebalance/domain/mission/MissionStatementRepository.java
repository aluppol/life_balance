package com.luppol.lifebalance.domain.mission;

import com.luppol.lifebalance.domain.person.PersonId;

import java.util.Optional;

public interface MissionStatementRepository {
    Optional<MissionStatement> findByOwner(PersonId owner);

    void save(MissionStatement statement);
}
