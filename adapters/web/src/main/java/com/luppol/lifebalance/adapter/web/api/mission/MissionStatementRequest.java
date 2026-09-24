package com.luppol.lifebalance.adapter.web.api.mission;

import com.luppol.lifebalance.domain.mission.MissionStatement;
import com.luppol.lifebalance.domain.person.PersonId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MissionStatementRequest(@NotBlank @Size(max = MissionStatement.MAXIMUM_TEXT_LENGTH) String text) {
    MissionStatement toStatement(PersonId owner) {
        return new MissionStatement(owner, text);
    }
}
