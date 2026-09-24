package com.luppol.lifebalance.domain.planning;

import com.luppol.lifebalance.domain.RuleViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WeekStartTest {
    private static final WeekStart WEEK = new WeekStart(LocalDate.of(2026, 9, 21));

    @Test
    void weekStart_mustBeAMonday() {
        assertThatThrownBy(() -> new WeekStart(LocalDate.of(2026, 9, 23)))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("A week starts on a Monday, 2026-09-23 is a WEDNESDAY");
    }

    @Test
    void weekStart_isRequired() {
        assertThatThrownBy(() -> new WeekStart(null)).hasMessage("Week start is required");
    }

    @ParameterizedTest
    @CsvSource({"2026-09-21, 2026-09-21", "2026-09-23, 2026-09-21", "2026-09-27, 2026-09-21", "2026-09-28, 2026-09-28"})
    void containing_findsTheMondayOfTheWeek(LocalDate date, LocalDate monday) {
        assertThat(WeekStart.containing(date).monday()).isEqualTo(monday);
    }

    @ParameterizedTest
    @CsvSource({"2026-09-20, false", "2026-09-21, true", "2026-09-24, true", "2026-09-27, true", "2026-09-28, false"})
    void includes_coversMondayToSunday(LocalDate date, boolean isIncluded) {
        assertThat(WEEK.includes(date)).isEqualTo(isIncluded);
    }

    @Test
    void sunday_isTheLastDay() {
        assertThat(WEEK.sunday()).isEqualTo(LocalDate.of(2026, 9, 27));
    }

    @Test
    void day_findsTheDateOfAWeekday() {
        assertThat(WEEK.day(DayOfWeek.MONDAY)).isEqualTo(LocalDate.of(2026, 9, 21));
        assertThat(WEEK.day(DayOfWeek.THURSDAY)).isEqualTo(LocalDate.of(2026, 9, 24));
    }

    @Test
    void previous_isTheWeekBefore() {
        assertThat(WEEK.previous()).isEqualTo(new WeekStart(LocalDate.of(2026, 9, 14)));
    }

    @Test
    void weekStart_printsItsMonday() {
        assertThat(WEEK).hasToString("2026-09-21");
    }
}
