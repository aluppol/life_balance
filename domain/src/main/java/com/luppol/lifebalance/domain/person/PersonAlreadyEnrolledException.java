package com.luppol.lifebalance.domain.person;

import com.luppol.lifebalance.domain.ConflictException;

public class PersonAlreadyEnrolledException extends ConflictException {
    public PersonAlreadyEnrolledException(PersonId id) {
        super("Person %s is already enrolled".formatted(id));
    }
}
