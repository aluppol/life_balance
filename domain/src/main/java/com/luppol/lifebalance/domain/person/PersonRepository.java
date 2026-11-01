package com.luppol.lifebalance.domain.person;

import java.util.List;

public interface PersonRepository {
    boolean isEnrolled(PersonId id);

    long countMembers();

    List<MemberActivity> findInactiveMembers(InactivityPeriod period);

    void enroll(Person person);

    void recordActivity(PersonId id);

    void removeAllGuests();

    void removeAllButNewestGuests(int newestToKeep);

    void removeInactiveMembers(InactivityPeriod period);
}
