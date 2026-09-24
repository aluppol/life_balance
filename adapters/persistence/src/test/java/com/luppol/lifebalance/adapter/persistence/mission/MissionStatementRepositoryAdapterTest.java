package com.luppol.lifebalance.adapter.persistence.mission;

import com.luppol.lifebalance.adapter.persistence.PersistenceTest;
import com.luppol.lifebalance.domain.mission.MissionStatement;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MissionStatementRepositoryAdapterTest extends PersistenceTest {
    @Test
    void save_storesAndReplacesTheStatement() {
        enroll(OWNER);

        missionStatements.save(new MissionStatement(OWNER, "First draft"));
        flushAndClear();
        missionStatements.save(new MissionStatement(OWNER, "Second draft"));
        flushAndClear();

        assertThat(missionStatements.findByOwner(OWNER)).contains(new MissionStatement(OWNER, "Second draft"));
    }

    @Test
    void findByOwner_isEmptyWithoutAStatement() {
        enroll(OWNER);

        assertThat(missionStatements.findByOwner(OWNER)).isEmpty();
    }
}
