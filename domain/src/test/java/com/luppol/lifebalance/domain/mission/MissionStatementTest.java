package com.luppol.lifebalance.domain.mission;

import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.person.PersonId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MissionStatementTest {
    private static final PersonId OWNER = new PersonId("owner");

    @Test
    void statement_keepsItsText() {
        assertThat(new MissionStatement(OWNER, "Live by principles").text()).isEqualTo("Live by principles");
    }

    @Test
    void statement_requiresAnOwner() {
        assertThatThrownBy(() -> new MissionStatement(null, "text")).hasMessage("Mission statement owner is required");
    }

    @Test
    void statement_rejectsBlankText() {
        assertThatThrownBy(() -> new MissionStatement(OWNER, " ")).isInstanceOf(RuleViolationException.class);
    }

    @Test
    void statement_acceptsTheMaximumLength() {
        String text = "m".repeat(MissionStatement.MAXIMUM_TEXT_LENGTH);
        assertThat(new MissionStatement(OWNER, text).text()).hasSize(4000);
    }

    @Test
    void statement_rejectsLongerText() {
        String text = "m".repeat(MissionStatement.MAXIMUM_TEXT_LENGTH + 1);
        assertThatThrownBy(() -> new MissionStatement(OWNER, text)).isInstanceOf(RuleViolationException.class);
    }
}
