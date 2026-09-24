package com.luppol.lifebalance.adapter.persistence.mission;

import com.luppol.lifebalance.domain.mission.MissionStatement;
import com.luppol.lifebalance.domain.mission.MissionStatementRepository;
import com.luppol.lifebalance.domain.person.PersonId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MissionStatementRepositoryAdapter implements MissionStatementRepository {
    private final MissionStatementJpaRepository missionStatements;

    public MissionStatementRepositoryAdapter(MissionStatementJpaRepository missionStatements) {
        this.missionStatements = missionStatements;
    }

    @Override
    public Optional<MissionStatement> findByOwner(PersonId owner) {
        return missionStatements.findById(owner.value()).map(MissionStatementEntity::toDomain);
    }

    @Override
    public void save(MissionStatement statement) {
        missionStatements.save(MissionStatementEntity.from(statement));
    }
}
