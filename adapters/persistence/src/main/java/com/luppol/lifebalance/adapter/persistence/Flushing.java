package com.luppol.lifebalance.adapter.persistence;

import com.luppol.lifebalance.domain.ConflictException;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;

public final class Flushing {
    private Flushing() {
    }

    public static void flushOrReportConflict(EntityManager entityManager, String conflict) {
        try {
            entityManager.flush();
        } catch (ConstraintViolationException violation) {
            throw new ConflictException(conflict);
        }
    }
}
