package com.luppol.lifebalance.adapter.persistence.value;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "core_value")
public class CoreValueEntity {
    @Id
    private UUID id;

    @Column(name = "person_id")
    private String personId;

    private String name;

    private String description;

    private int position;

    protected CoreValueEntity() {
    }

    private CoreValueEntity(UUID id, String personId, String name, String description, int position) {
        this.id = id;
        this.personId = personId;
        this.name = name;
        this.description = description;
        this.position = position;
    }

    static CoreValueEntity from(CoreValue value) {
        return new CoreValueEntity(value.id().value(), value.owner().value(), value.name(), value.description(),
                value.position());
    }

    CoreValue toDomain() {
        return new CoreValue(new CoreValueId(id), new PersonId(personId), name, description, position);
    }
}
