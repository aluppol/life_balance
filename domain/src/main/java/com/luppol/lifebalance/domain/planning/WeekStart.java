package com.luppol.lifebalance.domain.planning;

import com.luppol.lifebalance.domain.RuleViolationException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static com.luppol.lifebalance.domain.Invariants.requirePresent;

public record WeekStart(LocalDate monday) {
    private static final int LAST_DAY_OFFSET = 6;

    public WeekStart {
        requirePresent(monday, "Week start");
        if (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            throw new RuleViolationException("A week starts on a Monday, %s is a %s".formatted(monday, monday.getDayOfWeek()));
        }
    }

    public static WeekStart containing(LocalDate date) {
        return new WeekStart(date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
    }

    public LocalDate sunday() {
        return monday.plusDays(LAST_DAY_OFFSET);
    }

    public LocalDate day(DayOfWeek dayOfWeek) {
        return monday.plusDays(dayOfWeek.getValue() - 1L);
    }

    public boolean includes(LocalDate date) {
        return !date.isBefore(monday) && !date.isAfter(sunday());
    }

    public WeekStart previous() {
        return new WeekStart(monday.minusWeeks(1));
    }

    @Override
    public String toString() {
        return monday.toString();
    }
}
