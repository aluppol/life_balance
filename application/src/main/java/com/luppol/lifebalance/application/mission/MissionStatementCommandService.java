package com.luppol.lifebalance.application.mission;

import com.luppol.lifebalance.domain.mission.MissionStatement;
import com.luppol.lifebalance.domain.mission.MissionStatementRepository;

public class MissionStatementCommandService implements MissionStatementCommands {
    private final MissionStatementRepository missionStatements;

    public MissionStatementCommandService(MissionStatementRepository missionStatements) {
        this.missionStatements = missionStatements;
    }

    @Override
    public void define(MissionStatement statement) {
        missionStatements.save(statement);
    }
}
