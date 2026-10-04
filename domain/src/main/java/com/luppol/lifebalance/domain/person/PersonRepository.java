package com.luppol.lifebalance.domain.person;

public interface PersonRepository {
    boolean isEnrolled(PersonId id);

    void enroll(Person person);

    void removeAllGuests();

    void removeAllButNewestGuests(int newestToKeep);
}
