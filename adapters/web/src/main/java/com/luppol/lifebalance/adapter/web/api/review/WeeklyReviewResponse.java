package com.luppol.lifebalance.adapter.web.api.review;

import com.luppol.lifebalance.domain.review.RenewalDimension;
import com.luppol.lifebalance.domain.review.WeeklyReview;

import java.time.LocalDate;
import java.util.List;

public record WeeklyReviewResponse(LocalDate weekStart, String accomplishments, String lessons,
                                   List<RenewalDimension> renewedDimensions) {
    static WeeklyReviewResponse from(WeeklyReview review) {
        return new WeeklyReviewResponse(review.week().monday(), review.accomplishments(), review.lessons(),
                review.renewedDimensions().stream().sorted().toList());
    }
}
