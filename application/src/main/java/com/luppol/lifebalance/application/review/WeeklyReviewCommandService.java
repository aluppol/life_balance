package com.luppol.lifebalance.application.review;

import com.luppol.lifebalance.domain.review.WeeklyReview;
import com.luppol.lifebalance.domain.review.WeeklyReviewRepository;

public class WeeklyReviewCommandService implements WeeklyReviewCommands {
    private final WeeklyReviewRepository reviews;

    public WeeklyReviewCommandService(WeeklyReviewRepository reviews) {
        this.reviews = reviews;
    }

    @Override
    public void record(WeeklyReview review) {
        reviews.save(review);
    }
}
