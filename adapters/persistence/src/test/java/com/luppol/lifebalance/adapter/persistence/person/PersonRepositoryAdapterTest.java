package com.luppol.lifebalance.adapter.persistence.person;

import com.luppol.lifebalance.adapter.persistence.PersistenceTest;
import com.luppol.lifebalance.domain.person.Person;
import com.luppol.lifebalance.domain.person.PersonAlreadyEnrolledException;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonRepositoryAdapterTest extends PersistenceTest {
    @Test
    void enroll_makesThePersonKnown() {
        people.enroll(Person.member(OWNER));

        assertThat(people.isEnrolled(OWNER)).isTrue();
        assertThat(people.isEnrolled(STRANGER)).isFalse();
    }

    @Test
    void enroll_rejectsTheSameSubjectTwice() {
        people.enroll(Person.member(OWNER));
        flushAndClear();

        assertThatThrownBy(() -> people.enroll(Person.guest(OWNER)))
                .isInstanceOf(PersonAlreadyEnrolledException.class);
    }

    @Test
    void enroll_rejectsTheSameSubjectTwiceInOneSession() {
        people.enroll(Person.member(OWNER));

        assertThatThrownBy(() -> people.enroll(Person.member(OWNER)))
                .isInstanceOf(PersonAlreadyEnrolledException.class);
    }

    @Test
    void findGuests_listsOnlyGuests() {
        PersonId guest = new PersonId("guest");
        people.enroll(Person.member(OWNER));
        people.enroll(Person.guest(guest));

        assertThat(people.findGuests()).containsExactly(guest);
    }

    @Test
    void remove_deletesThePersonAndEverythingTheyOwn() {
        enroll(OWNER, STRANGER);
        coreValues.add(new CoreValue(CoreValueId.random(), OWNER, "Integrity", "", 0));
        coreValues.add(new CoreValue(CoreValueId.random(), STRANGER, "Integrity", "", 0));

        people.remove(OWNER);

        assertThat(people.isEnrolled(OWNER)).isFalse();
        assertThat(coreValues.findAllByOwner(OWNER)).isEmpty();
        assertThat(coreValues.findAllByOwner(STRANGER)).hasSize(1);
    }
}
