package com.luppol.life_balance.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserUsernamelValidationException extends RuntimeException {
    public UserUsernamelValidationException(String message) {
        super(String.format("Username validation error: %s", message));

    }
}
