package com.luppol.lifebalance.domain.person;

import com.luppol.lifebalance.domain.RuleViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonTest {
    private static final PersonId ID = new PersonId("f81d4fae-7dec-11d0-a765-00a0c91e6bf6");

    @Test
    void member_isAMember() {
        assertThat(Person.member(ID).kind()).isEqualTo(PersonKind.MEMBER);
    }

    @Test
    void guest_isAGuest() {
        assertThat(Person.guest(ID)).isEqualTo(new Person(ID, PersonKind.GUEST));
    }

    @Test
    void person_requiresAnId() {
        assertThatThrownBy(() -> new Person(null, PersonKind.MEMBER)).hasMessage("Person id is required");
    }

    @Test
    void person_requiresAKind() {
        assertThatThrownBy(() -> new Person(ID, null)).hasMessage("Person kind is required");
    }

    @Test
    void personId_rejectsBlank() {
        assertThatThrownBy(() -> new PersonId(" ")).isInstanceOf(RuleViolationException.class);
    }

    @Test
    void personId_rejectsMoreThan255Characters() {
        assertThatThrownBy(() -> new PersonId("x".repeat(256))).isInstanceOf(RuleViolationException.class);
    }

    @Test
    void personId_printsItsValue() {
        assertThat(ID).hasToString("f81d4fae-7dec-11d0-a765-00a0c91e6bf6");
    }

    @Test
    void alreadyEnrolled_namesThePerson() {
        assertThat(new PersonAlreadyEnrolledException(ID))
                .hasMessage("Person f81d4fae-7dec-11d0-a765-00a0c91e6bf6 is already enrolled");
    }
}
