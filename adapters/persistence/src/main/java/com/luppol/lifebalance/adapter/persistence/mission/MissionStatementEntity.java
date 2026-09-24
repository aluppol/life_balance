package com.luppol.lifebalance.adapter.persistence.mission;

import com.luppol.lifebalance.domain.mission.MissionStatement;
import com.luppol.lifebalance.domain.person.PersonId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mission_statement")
public class MissionStatementEntity {
    @Id
    @Column(name = "person_id")
    private String personId;

    private String text;

    protected MissionStatementEntity() {
    }

    private MissionStatementEntity(String personId, String text) {
        this.personId = personId;
        this.text = text;
    }

    static MissionStatementEntity from(MissionStatement statement) {
        return new MissionStatementEntity(statement.owner().value(), statement.text());
    }

    MissionStatement toDomain() {
        return new MissionStatement(new PersonId(personId), text);
    }
}
