package com.luppol.lifebalance.application.mission;

import com.luppol.lifebalance.domain.NotFoundException;
import com.luppol.lifebalance.domain.mission.MissionStatement;
import com.luppol.lifebalance.domain.mission.MissionStatementRepository;
import com.luppol.lifebalance.domain.person.PersonId;

public class MissionStatementQueryService implements MissionStatementQueries {
    private final MissionStatementRepository missionStatements;

    public MissionStatementQueryService(MissionStatementRepository missionStatements) {
        this.missionStatements = missionStatements;
    }

    @Override
    public MissionStatement find(PersonId owner) {
        return missionStatements.findByOwner(owner)
                .orElseThrow(() -> new NotFoundException("Mission statement of", owner));
    }
}
