package com.luppol.lifebalance.adapter.web.api.review;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.review.RenewalDimension;
import com.luppol.lifebalance.domain.review.WeeklyReview;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;
import java.util.Set;

public record WeeklyReviewRequest(
        @Size(max = WeeklyReview.MAXIMUM_TEXT_LENGTH) String accomplishments,
        @Size(max = WeeklyReview.MAXIMUM_TEXT_LENGTH) String lessons,
        @NotNull Set<@NotNull RenewalDimension> renewedDimensions) {

    WeeklyReview toReview(PersonId owner, WeekStart week) {
        return new WeeklyReview(owner, week, Objects.requireNonNullElse(accomplishments, ""),
                Objects.requireNonNullElse(lessons, ""), renewedDimensions);
    }
}
