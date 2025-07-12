package com.luppol.life_balance.exceptions;

public class PersonAlreadyHasMissionException extends RuntimeException {
    public PersonAlreadyHasMissionException(Long personId) {
        super(String.format("Person with id %d already has a mission assigned.", personId));
    }
}
