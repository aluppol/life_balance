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
    private static final PersonId OLDEST_VISIT = new PersonId("guest:oldest-session");
    private static final PersonId MIDDLE_VISIT = new PersonId("guest:middle-session");
    private static final PersonId NEWEST_VISIT = new PersonId("guest:newest-session");

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
    void removeAllGuests_deletesEveryGuestWithEverythingTheyOwnAndSparesMembers() {
        people.enroll(Person.guest(OLDEST_VISIT));
        people.enroll(Person.member(OWNER));
        people.enroll(Person.guest(NEWEST_VISIT));
        coreValues.add(new CoreValue(CoreValueId.random(), OLDEST_VISIT, "Integrity", "", 0));
        coreValues.add(new CoreValue(CoreValueId.random(), OWNER, "Integrity", "", 0));
        coreValues.add(new CoreValue(CoreValueId.random(), NEWEST_VISIT, "Integrity", "", 0));

        people.removeAllGuests();

        assertThat(people.isEnrolled(OLDEST_VISIT)).isFalse();
        assertThat(people.isEnrolled(NEWEST_VISIT)).isFalse();
        assertThat(coreValues.findAllByOwner(OLDEST_VISIT)).isEmpty();
        assertThat(coreValues.findAllByOwner(NEWEST_VISIT)).isEmpty();
        assertThat(people.isEnrolled(OWNER)).isTrue();
        assertThat(coreValues.findAllByOwner(OWNER)).hasSize(1);
    }

    @Test
    void removeAllButNewestGuests_deletesTheOldestGuestsInEnrollmentOrderAndSparesMembers() {
        people.enroll(Person.guest(OLDEST_VISIT));
        people.enroll(Person.member(OWNER));
        people.enroll(Person.guest(MIDDLE_VISIT));
        people.enroll(Person.guest(NEWEST_VISIT));
        coreValues.add(new CoreValue(CoreValueId.random(), OLDEST_VISIT, "Integrity", "", 0));

        people.removeAllButNewestGuests(2);

        assertThat(people.isEnrolled(OLDEST_VISIT)).isFalse();
        assertThat(coreValues.findAllByOwner(OLDEST_VISIT)).isEmpty();
        assertThat(people.isEnrolled(MIDDLE_VISIT)).isTrue();
        assertThat(people.isEnrolled(NEWEST_VISIT)).isTrue();
        assertThat(people.isEnrolled(OWNER)).isTrue();
    }

    @Test
    void removeAllButNewestGuests_keepsEveryGuestWhileThereIsRoom() {
        people.enroll(Person.guest(OLDEST_VISIT));
        people.enroll(Person.guest(NEWEST_VISIT));

        people.removeAllButNewestGuests(2);

        assertThat(people.isEnrolled(OLDEST_VISIT)).isTrue();
        assertThat(people.isEnrolled(NEWEST_VISIT)).isTrue();
    }
}
