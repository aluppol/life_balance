package com.luppol.life_balance.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PersonAlreadyHasMissionException extends RuntimeException {
    public PersonAlreadyHasMissionException(Long personId) {
        super(String.format("Person with id %d already has a mission assigned.", personId));
    }
}
