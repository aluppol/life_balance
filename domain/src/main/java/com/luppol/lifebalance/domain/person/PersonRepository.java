package com.luppol.lifebalance.domain.person;

import java.util.List;

public interface PersonRepository {
    boolean isEnrolled(PersonId id);

    List<PersonId> findGuests();

    void enroll(Person person);

    void remove(PersonId id);
}
