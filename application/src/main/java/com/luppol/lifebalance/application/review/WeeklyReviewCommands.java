package com.luppol.lifebalance.application.review;

import com.luppol.lifebalance.domain.review.WeeklyReview;

public interface WeeklyReviewCommands {
    void record(WeeklyReview review);
}
