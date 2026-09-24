package com.luppol.lifebalance.adapter.web.api.mission;

import com.luppol.lifebalance.domain.mission.MissionStatement;

public record MissionStatementResponse(String text) {
    static MissionStatementResponse from(MissionStatement statement) {
        return new MissionStatementResponse(statement.text());
    }
}
