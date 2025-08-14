package com.luppol.life_balance.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserPasswordValidationException extends RuntimeException {
    public UserPasswordValidationException(List<String> errors) {
        super("Password validation exceptions:\n\t%s".formatted(String.join("\n\t", errors)));

    }
}
