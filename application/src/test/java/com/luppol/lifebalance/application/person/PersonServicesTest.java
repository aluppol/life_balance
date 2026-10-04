package com.luppol.lifebalance.application.person;

import com.luppol.lifebalance.application.demo.DemoWorkspace;
import com.luppol.lifebalance.application.fakes.InMemoryPersonRepository;
import com.luppol.lifebalance.application.fakes.PlannerStore;
import com.luppol.lifebalance.domain.mission.MissionStatement;
import com.luppol.lifebalance.domain.person.GuestWorkspaceLimit;
import com.luppol.lifebalance.domain.person.Person;
import com.luppol.lifebalance.domain.person.PersonAlreadyEnrolledException;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonServicesTest {
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-23T15:00:00Z"), ZoneOffset.UTC);
    private static final PersonId MEMBER = new PersonId("member");
    private static final PersonId GUEST = new PersonId("guest");
    private static final PersonId FIRST_VISIT = new PersonId("guest:first-session");
    private static final PersonId SECOND_VISIT = new PersonId("guest:second-session");
    private static final PersonId THIRD_VISIT = new PersonId("guest:third-session");

    private final PlannerStore store = new PlannerStore();
    private final InMemoryPersonRepository people = new InMemoryPersonRepository(store);
    private final PersonCommands commands = new PersonCommandService(people, store.lifeRoles(),
            new DemoWorkspace(store.repositories(), CLOCK), new GuestWorkspaceLimit(2));
    private final PersonQueries queries = new PersonQueryService(people);

    @Test
    void enrollMember_startsWithOnlySharpenTheSaw() {
        commands.enrollMember(MEMBER);

        assertThat(queries.isEnrolled(MEMBER)).isTrue();
        assertThat(people.find(MEMBER)).isEqualTo(Person.member(MEMBER));
        assertThat(store.lifeRoles().findAllByOwner(MEMBER)).extracting(LifeRole::name).containsExactly("Sharpen the Saw");
        assertThat(store.missionStatements().findByOwner(MEMBER)).isEmpty();
    }

    @Test
    void enrollGuest_furnishesTheDemoWorkspace() {
        commands.enrollGuest(GUEST);

        assertThat(people.find(GUEST)).isEqualTo(Person.guest(GUEST));
        assertThat(store.lifeRoles().findAllByOwner(GUEST)).hasSize(5);
        assertThat(store.missionStatements().findByOwner(GUEST)).isPresent();
    }

    @Test
    void enrollGuest_belowTheLimit_keepsEveryGuest() {
        commands.enrollGuest(FIRST_VISIT);

        commands.enrollGuest(SECOND_VISIT);

        assertThat(queries.isEnrolled(FIRST_VISIT)).isTrue();
        assertThat(queries.isEnrolled(SECOND_VISIT)).isTrue();
    }

    @Test
    void enrollGuest_atTheLimit_removesTheOldestGuestWithItsWorkspace() {
        commands.enrollMember(MEMBER);
        commands.enrollGuest(FIRST_VISIT);
        commands.enrollGuest(SECOND_VISIT);

        commands.enrollGuest(THIRD_VISIT);

        assertThat(queries.isEnrolled(FIRST_VISIT)).isFalse();
        assertThat(store.missionStatements().findByOwner(FIRST_VISIT)).isEmpty();
        assertThat(store.lifeRoles().findAllByOwner(FIRST_VISIT)).isEmpty();
        assertThat(queries.isEnrolled(SECOND_VISIT)).isTrue();
        assertThat(queries.isEnrolled(THIRD_VISIT)).isTrue();
        assertThat(queries.isEnrolled(MEMBER)).isTrue();
    }

    @Test
    void enroll_rejectsASecondEnrollment() {
        commands.enrollMember(MEMBER);

        assertThatThrownBy(() -> commands.enrollGuest(MEMBER)).isInstanceOf(PersonAlreadyEnrolledException.class);
    }

    @Test
    void isEnrolled_isFalseForAStranger() {
        assertThat(queries.isEnrolled(new PersonId("stranger"))).isFalse();
    }

    @Test
    void resetGuestWorkspaces_removesEveryGuestAndLeavesMembersAlone() {
        commands.enrollMember(MEMBER);
        commands.enrollGuest(FIRST_VISIT);
        commands.enrollGuest(SECOND_VISIT);
        store.missionStatements().save(new MissionStatement(MEMBER, "Mine"));
        store.coreValues().add(new CoreValue(CoreValueId.random(), FIRST_VISIT, "Spam", "", 9));

        commands.resetGuestWorkspaces();

        assertThat(queries.isEnrolled(FIRST_VISIT)).isFalse();
        assertThat(queries.isEnrolled(SECOND_VISIT)).isFalse();
        assertThat(store.coreValues().findAllByOwner(FIRST_VISIT)).isEmpty();
        assertThat(store.missionStatements().findByOwner(SECOND_VISIT)).isEmpty();
        assertThat(store.missionStatements().findByOwner(MEMBER).orElseThrow().text()).isEqualTo("Mine");
        assertThat(queries.isEnrolled(MEMBER)).isTrue();
    }

    @Test
    void enrollGuest_afterAReset_furnishesAFreshWorkspace() {
        commands.enrollGuest(GUEST);
        store.missionStatements().save(new MissionStatement(GUEST, "Defaced"));
        commands.resetGuestWorkspaces();

        commands.enrollGuest(GUEST);

        assertThat(store.missionStatements().findByOwner(GUEST).orElseThrow().text()).startsWith("I live by principles");
        assertThat(store.coreValues().findAllByOwner(GUEST)).hasSize(4);
    }
}
