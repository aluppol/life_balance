package com.luppol.lifebalance.domain;

public final class Invariants {
    private Invariants() {
    }

    public static void requirePresent(Object value, String subject) {
        if (value == null) {
            throw new RuleViolationException("%s is required".formatted(subject));
        }
    }

    public static void requireText(String value, String subject, int maximumLength) {
        requirePresent(value, subject);
        if (value.isBlank()) {
            throw new RuleViolationException("%s must not be blank".formatted(subject));
        }
        requireMaximumLength(value, subject, maximumLength);
    }

    public static void requireMaximumLength(String value, String subject, int maximumLength) {
        requirePresent(value, subject);
        if (value.length() > maximumLength) {
            throw new RuleViolationException("%s must be at most %d characters".formatted(subject, maximumLength));
        }
    }

    public static void requireNotNegative(int value, String subject) {
        if (value < 0) {
            throw new RuleViolationException("%s must not be negative".formatted(subject));
        }
    }
}
