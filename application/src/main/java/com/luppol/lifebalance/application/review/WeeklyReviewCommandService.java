package com.luppol.lifebalance.application.review;

import com.luppol.lifebalance.domain.Invariants;
import com.luppol.lifebalance.domain.review.WeeklyReview;
import com.luppol.lifebalance.domain.review.WeeklyReviewRepository;

public class WeeklyReviewCommandService implements WeeklyReviewCommands {
    private final WeeklyReviewRepository reviews;

    public WeeklyReviewCommandService(WeeklyReviewRepository reviews) {
        this.reviews = reviews;
    }

    @Override
    public void record(WeeklyReview review) {
        if (reviews.findByWeek(review.owner(), review.week()).isEmpty()) {
            Invariants.requireRoomForOneMore(reviews.countAllByOwner(review.owner()), WeeklyReview.MAXIMUM_PER_PERSON,
                    "weekly reviews");
        }
        reviews.save(review);
    }
}
