package com.luppol.lifebalance.application.person;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.person.PersonRepository;

public class PersonQueryService implements PersonQueries {
    private final PersonRepository people;

    public PersonQueryService(PersonRepository people) {
        this.people = people;
    }

    @Override
    public boolean isEnrolled(PersonId id) {
        return people.isEnrolled(id);
    }
}
