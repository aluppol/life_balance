package com.luppol.lifebalance.domain;

public class NotFoundException extends DomainException {
    public NotFoundException(String subject, Object id) {
        super("%s %s not found".formatted(subject, id));
    }
}
