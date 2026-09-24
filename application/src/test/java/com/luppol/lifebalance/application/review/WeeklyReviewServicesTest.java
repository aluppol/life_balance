package com.luppol.lifebalance.application.review;

import com.luppol.lifebalance.application.fakes.PlannerStore;
import com.luppol.lifebalance.domain.NotFoundException;
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
    void find_reportsAMissingReview() {
        assertThatThrownBy(() -> queries.find(OWNER, WEEK))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Weekly review of the week starting 2026-09-21 not found");
    }
}
