package com.luppol.lifebalance.adapter.persistence.person;

import com.luppol.lifebalance.domain.person.MemberActivity;
import com.luppol.lifebalance.domain.person.Person;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.person.PersonKind;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "person")
public class PersonEntity {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private PersonKind kind;

    @Column(name = "enrolled_at", insertable = false, updatable = false)
    private Instant enrolledAt;

    @Column(name = "last_active_at", insertable = false, updatable = false)
    private Instant lastActiveAt;

    protected PersonEntity() {
    }

    private PersonEntity(String id, PersonKind kind) {
        this.id = id;
        this.kind = kind;
    }

    static PersonEntity from(Person person) {
        return new PersonEntity(person.id().value(), person.kind());
    }

    MemberActivity toMemberActivity() {
        return new MemberActivity(new PersonId(id), lastActiveAt);
    }
}
