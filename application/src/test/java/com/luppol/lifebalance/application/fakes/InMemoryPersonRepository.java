package com.luppol.lifebalance.application.fakes;

import com.luppol.lifebalance.domain.person.InactivityPeriod;
import com.luppol.lifebalance.domain.person.MemberActivity;
import com.luppol.lifebalance.domain.person.Person;
import com.luppol.lifebalance.domain.person.PersonAlreadyEnrolledException;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.person.PersonKind;
import com.luppol.lifebalance.domain.person.PersonRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryPersonRepository implements PersonRepository {
    private final Map<PersonId, Person> people = new LinkedHashMap<>();
    private final Map<PersonId, Instant> lastActiveTimes = new HashMap<>();
    private final PlannerStore store;
    private Instant now = Instant.parse("2026-09-23T15:00:00Z");

    public InMemoryPersonRepository(PlannerStore store) {
        this.store = store;
    }

    @Override
    public boolean isEnrolled(PersonId id) {
        return people.containsKey(id);
    }

    @Override
    public long countMembers() {
        return people.values().stream().filter(person -> person.kind() == PersonKind.MEMBER).count();
    }

    @Override
    public List<MemberActivity> findInactiveMembers(InactivityPeriod period) {
        Instant activeSince = now.minus(Duration.ofDays(period.days()));
        return people.values().stream()
                .filter(person -> person.kind() == PersonKind.MEMBER)
                .map(person -> new MemberActivity(person.id(), lastActiveTimes.get(person.id())))
                .filter(member -> member.lastActiveAt().isBefore(activeSince))
                .sorted(Comparator.comparing(MemberActivity::lastActiveAt))
                .toList();
    }

    @Override
    public void enroll(Person person) {
        if (people.putIfAbsent(person.id(), person) != null) {
            throw new PersonAlreadyEnrolledException(person.id());
        }
        lastActiveTimes.put(person.id(), now);
    }

    @Override
    public void recordActivity(PersonId id) {
        lastActiveTimes.replace(id, now);
    }

    @Override
    public void removeAllGuests() {
        guestsOldestFirst().forEach(this::remove);
    }

    @Override
    public void removeAllButNewestGuests(int newestToKeep) {
        List<PersonId> guests = guestsOldestFirst();
        guests.subList(0, Math.max(0, guests.size() - newestToKeep)).forEach(this::remove);
    }

    @Override
    public void removeInactiveMembers(InactivityPeriod period) {
        findInactiveMembers(period).forEach(member -> remove(member.id()));
    }

    public Person find(PersonId id) {
        return people.get(id);
    }

    public Instant now() {
        return now;
    }

    public void advanceClockBy(Duration duration) {
        now = now.plus(duration);
    }

    private List<PersonId> guestsOldestFirst() {
        return people.values().stream().filter(person -> person.kind() == PersonKind.GUEST).map(Person::id).toList();
    }

    private void remove(PersonId id) {
        people.remove(id);
        lastActiveTimes.remove(id);
        store.removeEverythingOf(id);
    }
}
