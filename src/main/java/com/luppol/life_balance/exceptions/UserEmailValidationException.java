package com.luppol.life_balance.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserEmailValidationException extends RuntimeException {
    public UserEmailValidationException(String message) {
        super(String.format("Email validation error: %s", message));

    }
}
