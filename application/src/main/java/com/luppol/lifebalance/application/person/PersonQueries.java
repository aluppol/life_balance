package com.luppol.lifebalance.application.person;

import com.luppol.lifebalance.domain.person.PersonId;

public interface PersonQueries {
    boolean isEnrolled(PersonId id);
}
