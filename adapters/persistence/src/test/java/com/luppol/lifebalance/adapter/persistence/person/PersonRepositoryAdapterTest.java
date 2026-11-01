package com.luppol.lifebalance.adapter.persistence.person;

import com.luppol.lifebalance.adapter.persistence.PersistenceTest;
import com.luppol.lifebalance.domain.person.InactivityPeriod;
import com.luppol.lifebalance.domain.person.MemberActivity;
import com.luppol.lifebalance.domain.person.Person;
import com.luppol.lifebalance.domain.person.PersonAlreadyEnrolledException;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonRepositoryAdapterTest extends PersistenceTest {
    private static final PersonId OLDEST_VISIT = new PersonId("guest:oldest-session");
    private static final PersonId MIDDLE_VISIT = new PersonId("guest:middle-session");
    private static final PersonId NEWEST_VISIT = new PersonId("guest:newest-session");
    private static final PersonId ACTIVE_MEMBER = new PersonId("active-member");
    private static final InactivityPeriod A_YEAR = new InactivityPeriod(365);
    private static final String YESTERDAY = "date_trunc('day', clock_timestamp()) - interval '1 second'";

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

    @Test
    void enroll_startsThePersonActive() {
        enroll(OWNER);

        assertThat(people.findInactiveMembers(new InactivityPeriod(1))).isEmpty();
        assertThat(people.countMembers()).isEqualTo(1);
    }

    @Test
    void recordActivity_movesAnEarlierDayToNow() {
        enroll(OWNER);
        setLastActive(OWNER, YESTERDAY);
        Instant yesterday = lastActiveAt(OWNER);

        people.recordActivity(OWNER);

        assertThat(lastActiveAt(OWNER)).isAfterOrEqualTo(yesterday.plusSeconds(1));
    }

    @Test
    void recordActivity_writesAtMostOncePerDay() {
        enroll(OWNER);
        setLastActive(OWNER, YESTERDAY);
        people.recordActivity(OWNER);
        Instant firstToday = lastActiveAt(OWNER);

        people.recordActivity(OWNER);

        assertThat(lastActiveAt(OWNER)).isEqualTo(firstToday);
    }

    @Test
    void recordActivity_ignoresAStranger() {
        people.recordActivity(STRANGER);

        assertThat(people.isEnrolled(STRANGER)).isFalse();
    }

    @Test
    void findInactiveMembers_listsMembersIdleForThePeriodOldestFirstAndSkipsGuests() {
        enroll(OWNER, STRANGER, ACTIVE_MEMBER);
        people.enroll(Person.guest(OLDEST_VISIT));
        setLastActive(OWNER, "clock_timestamp() - interval '366 days'");
        setLastActive(STRANGER, "clock_timestamp() - interval '500 days'");
        setLastActive(ACTIVE_MEMBER, "clock_timestamp() - interval '364 days'");
        setLastActive(OLDEST_VISIT, "clock_timestamp() - interval '600 days'");

        assertThat(people.findInactiveMembers(A_YEAR)).containsExactly(
                new MemberActivity(STRANGER, lastActiveAt(STRANGER)), new MemberActivity(OWNER, lastActiveAt(OWNER)));
        assertThat(people.countMembers()).isEqualTo(3);
    }

    @Test
    void removeInactiveMembers_deletesThemWithEverythingTheyOwnAndSparesGuestsAndActiveMembers() {
        enroll(OWNER, ACTIVE_MEMBER);
        people.enroll(Person.guest(OLDEST_VISIT));
        coreValues.add(new CoreValue(CoreValueId.random(), OWNER, "Integrity", "", 0));
        setLastActive(OWNER, "clock_timestamp() - interval '366 days'");
        setLastActive(ACTIVE_MEMBER, "clock_timestamp() - interval '364 days'");
        setLastActive(OLDEST_VISIT, "clock_timestamp() - interval '600 days'");

        people.removeInactiveMembers(A_YEAR);

        assertThat(people.isEnrolled(OWNER)).isFalse();
        assertThat(coreValues.findAllByOwner(OWNER)).isEmpty();
        assertThat(people.isEnrolled(ACTIVE_MEMBER)).isTrue();
        assertThat(people.isEnrolled(OLDEST_VISIT)).isTrue();
        assertThat(people.countMembers()).isEqualTo(1);
    }

    private void setLastActive(PersonId id, String time) {
        flushAndClear();
        entityManager.createNativeQuery("update core.person set last_active_at = %s where id = :id".formatted(time))
                .setParameter("id", id.value())
                .executeUpdate();
    }

    private Instant lastActiveAt(PersonId id) {
        flushAndClear();
        return entityManager.find(PersonEntity.class, id.value()).toMemberActivity().lastActiveAt();
    }
}
