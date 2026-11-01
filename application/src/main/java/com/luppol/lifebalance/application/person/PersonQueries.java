package com.luppol.lifebalance.application.person;

import com.luppol.lifebalance.domain.person.InactivityPeriod;
import com.luppol.lifebalance.domain.person.MemberActivity;
import com.luppol.lifebalance.domain.person.PersonId;

import java.util.List;

public interface PersonQueries {
    boolean isEnrolled(PersonId id);

    long countMembers();

    List<MemberActivity> findInactiveMembers(InactivityPeriod period);
}
