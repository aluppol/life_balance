package com.luppol.lifebalance.application.mission;

import com.luppol.lifebalance.domain.mission.MissionStatement;
import com.luppol.lifebalance.domain.person.PersonId;

public interface MissionStatementQueries {
    MissionStatement find(PersonId owner);
}
