package com.luppol.lifebalance.domain.review;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.WeekStart;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WeeklyReviewTest {
    private static final PersonId OWNER = new PersonId("owner");
    private static final WeekStart WEEK = new WeekStart(LocalDate.of(2026, 9, 21));

    @Test
    void review_keepsItsAnswers() {
        WeeklyReview review = new WeeklyReview(OWNER, WEEK, "Shipped", "Sleep more", Set.of(RenewalDimension.PHYSICAL));
        assertThat(review.accomplishments()).isEqualTo("Shipped");
        assertThat(review.lessons()).isEqualTo("Sleep more");
        assertThat(review.renewedDimensions()).containsExactly(RenewalDimension.PHYSICAL);
    }

    @Test
    void review_copiesItsDimensions() {
        Set<RenewalDimension> dimensions = EnumSet.of(RenewalDimension.MENTAL);
        WeeklyReview review = new WeeklyReview(OWNER, WEEK, "", "", dimensions);
        dimensions.add(RenewalDimension.SPIRITUAL);
        assertThat(review.renewedDimensions()).containsExactly(RenewalDimension.MENTAL);
    }

    @Test
    void review_validatesItsFields() {
        Set<RenewalDimension> none = Set.of();
        assertThatThrownBy(() -> new WeeklyReview(null, WEEK, "", "", none)).hasMessage("Weekly review owner is required");
        assertThatThrownBy(() -> new WeeklyReview(OWNER, null, "", "", none)).hasMessage("Weekly review week is required");
        assertThatThrownBy(() -> new WeeklyReview(OWNER, WEEK, "a".repeat(4001), "", none))
                .hasMessage("Accomplishments must be at most 4000 characters");
        assertThatThrownBy(() -> new WeeklyReview(OWNER, WEEK, "", "l".repeat(4001), none))
                .hasMessage("Lessons must be at most 4000 characters");
        assertThatThrownBy(() -> new WeeklyReview(OWNER, WEEK, "", "", null)).hasMessage("Renewed dimensions is required");
    }
}
