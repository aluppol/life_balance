package com.luppol.lifebalance.application.mission;

import com.luppol.lifebalance.application.fakes.PlannerStore;
import com.luppol.lifebalance.domain.NotFoundException;
import com.luppol.lifebalance.domain.mission.MissionStatement;
import com.luppol.lifebalance.domain.person.PersonId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MissionStatementServicesTest {
    private static final PersonId OWNER = new PersonId("owner");

    private final PlannerStore store = new PlannerStore();
    private final MissionStatementCommands commands = new MissionStatementCommandService(store.missionStatements());
    private final MissionStatementQueries queries = new MissionStatementQueryService(store.missionStatements());

    @Test
    void define_replacesTheStatement() {
        commands.define(new MissionStatement(OWNER, "First draft"));
        commands.define(new MissionStatement(OWNER, "Second draft"));

        assertThat(queries.find(OWNER).text()).isEqualTo("Second draft");
    }

    @Test
    void find_reportsAMissingStatement() {
        assertThatThrownBy(() -> queries.find(OWNER))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Mission statement of owner not found");
    }
}
