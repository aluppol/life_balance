package com.luppol.lifebalance.application.review;

import com.luppol.lifebalance.application.fakes.PlannerStore;
import com.luppol.lifebalance.domain.NotFoundException;
import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.review.RenewalDimension;
import com.luppol.lifebalance.domain.review.WeeklyReview;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WeeklyReviewServicesTest {
    private static final PersonId OWNER = new PersonId("owner");
    private static final WeekStart WEEK = new WeekStart(LocalDate.of(2026, 9, 21));

    private final PlannerStore store = new PlannerStore();
    private final WeeklyReviewCommands commands = new WeeklyReviewCommandService(store.reviews());
    private final WeeklyReviewQueries queries = new WeeklyReviewQueryService(store.reviews());

    @Test
    void record_replacesTheReviewOfTheWeek() {
        commands.record(new WeeklyReview(OWNER, WEEK, "Draft", "", Set.of()));
        WeeklyReview review = new WeeklyReview(OWNER, WEEK, "Shipped", "Sleep more", Set.of(RenewalDimension.SPIRITUAL));
        commands.record(review);

        assertThat(queries.find(OWNER, WEEK)).isEqualTo(review);
    }

    @Test
    void record_rejectsANewWeekBeyondThePersonalMaximum() {
        reviewWeeksBefore(WEEK, WeeklyReview.MAXIMUM_PER_PERSON);
        WeeklyReview review = new WeeklyReview(OWNER, WEEK, "One too many", "", Set.of());

        assertThatThrownBy(() -> commands.record(review))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("A person can keep at most 520 weekly reviews");
        assertThat(store.reviews().findByWeek(OWNER, WEEK)).isEmpty();
    }

    @Test
    void record_rewritesAnExistingWeekAtTheMaximum() {
        commands.record(new WeeklyReview(OWNER, WEEK, "Draft", "", Set.of()));
        reviewWeeksBefore(WEEK, WeeklyReview.MAXIMUM_PER_PERSON - 1);
        WeeklyReview rewritten = new WeeklyReview(OWNER, WEEK, "Rewritten", "", Set.of());

        commands.record(rewritten);

        assertThat(queries.find(OWNER, WEEK)).isEqualTo(rewritten);
    }

    @Test
    void find_reportsAMissingReview() {
        assertThatThrownBy(() -> queries.find(OWNER, WEEK))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Weekly review of the week starting 2026-09-21 not found");
    }

    private void reviewWeeksBefore(WeekStart week, int count) {
        WeekStart reviewed = week;
        for (int index = 0; index < count; index++) {
            reviewed = reviewed.previous();
            store.reviews().save(new WeeklyReview(OWNER, reviewed, "", "", Set.of()));
        }
    }
}
