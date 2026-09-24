package com.luppol.lifebalance.application.fakes;

import com.luppol.lifebalance.domain.person.Person;
import com.luppol.lifebalance.domain.person.PersonAlreadyEnrolledException;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.person.PersonKind;
import com.luppol.lifebalance.domain.person.PersonRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryPersonRepository implements PersonRepository {
    private final Map<PersonId, Person> people = new LinkedHashMap<>();
    private final PlannerStore store;

    public InMemoryPersonRepository(PlannerStore store) {
        this.store = store;
    }

    @Override
    public boolean isEnrolled(PersonId id) {
        return people.containsKey(id);
    }

    @Override
    public List<PersonId> findGuests() {
        return people.values().stream().filter(person -> person.kind() == PersonKind.GUEST).map(Person::id).toList();
    }

    @Override
    public void enroll(Person person) {
        if (people.putIfAbsent(person.id(), person) != null) {
            throw new PersonAlreadyEnrolledException(person.id());
        }
    }

    @Override
    public void remove(PersonId id) {
        people.remove(id);
        store.removeEverythingOf(id);
    }

    public Person find(PersonId id) {
        return people.get(id);
    }
}
