package com.luppol.life_balance.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleDuplicatePerson_returnsConflictAndMessage() {
        // given
        String msg = "Person already exists";
        DuplicatePersonException ex = new DuplicatePersonException(msg);

        // when
        ResponseEntity<String> response = handler.handleDuplicatePerson(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo(msg);
    }
}
