package com.luppol.lifebalance.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {
    @Test
    void notFound_namesTheSubjectAndTheId() {
        assertThat(new NotFoundException("Goal", 42)).hasMessage("Goal 42 not found");
    }

    @Test
    void conflict_keepsItsMessage() {
        assertThat(new ConflictException("Taken")).hasMessage("Taken").isInstanceOf(DomainException.class);
    }
}
