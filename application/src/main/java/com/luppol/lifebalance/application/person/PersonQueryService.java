package com.luppol.lifebalance.application.person;

import com.luppol.lifebalance.domain.person.InactivityPeriod;
import com.luppol.lifebalance.domain.person.MemberActivity;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.person.PersonRepository;

import java.util.List;

public class PersonQueryService implements PersonQueries {
    private final PersonRepository people;

    public PersonQueryService(PersonRepository people) {
        this.people = people;
    }

    @Override
    public boolean isEnrolled(PersonId id) {
        return people.isEnrolled(id);
    }

    @Override
    public long countMembers() {
        return people.countMembers();
    }

    @Override
    public List<MemberActivity> findInactiveMembers(InactivityPeriod period) {
        return people.findInactiveMembers(period);
    }
}
