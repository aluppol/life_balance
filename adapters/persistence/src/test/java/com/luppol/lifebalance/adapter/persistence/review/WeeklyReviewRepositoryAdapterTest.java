package com.luppol.lifebalance.adapter.persistence.review;

import com.luppol.lifebalance.adapter.persistence.PersistenceTest;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.review.RenewalDimension;
import com.luppol.lifebalance.domain.review.WeeklyReview;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class WeeklyReviewRepositoryAdapterTest extends PersistenceTest {
    private static final WeekStart WEEK = new WeekStart(LocalDate.of(2026, 9, 21));

    @Test
    void save_storesAndReplacesTheReview() {
        enroll(OWNER, STRANGER);
        reviews.save(new WeeklyReview(OWNER, WEEK, "Draft", "", Set.of(RenewalDimension.MENTAL)));
        flushAndClear();

        WeeklyReview replacement = new WeeklyReview(OWNER, WEEK, "Shipped", "Sleep more",
                Set.of(RenewalDimension.PHYSICAL, RenewalDimension.SPIRITUAL));
        reviews.save(replacement);
        flushAndClear();

        assertThat(reviews.findByWeek(OWNER, WEEK)).contains(replacement);
        assertThat(reviews.findByWeek(OWNER, WEEK.previous())).isEmpty();
        assertThat(reviews.findByWeek(STRANGER, WEEK)).isEmpty();
    }
}
