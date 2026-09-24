package com.luppol.lifebalance.adapter.persistence.role;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.role.LifeRoleKind;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "life_role")
public class LifeRoleEntity {
    @Id
    private UUID id;

    @Column(name = "person_id")
    private String personId;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private LifeRoleKind kind;

    private int position;

    protected LifeRoleEntity() {
    }

    private LifeRoleEntity(LifeRole role) {
        this.id = role.id().value();
        this.personId = role.owner().value();
        this.name = role.name();
        this.description = role.description();
        this.kind = role.kind();
        this.position = role.position();
    }

    static LifeRoleEntity from(LifeRole role) {
        return new LifeRoleEntity(role);
    }

    LifeRole toDomain() {
        return new LifeRole(new LifeRoleId(id), new PersonId(personId), name, description, kind, position);
    }
}
