package com.luppol.lifebalance.application.review;

import com.luppol.lifebalance.domain.NotFoundException;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.review.WeeklyReview;
import com.luppol.lifebalance.domain.review.WeeklyReviewRepository;

public class WeeklyReviewQueryService implements WeeklyReviewQueries {
    private final WeeklyReviewRepository reviews;

    public WeeklyReviewQueryService(WeeklyReviewRepository reviews) {
        this.reviews = reviews;
    }

    @Override
    public WeeklyReview find(PersonId owner, WeekStart week) {
        return reviews.findByWeek(owner, week)
                .orElseThrow(() -> new NotFoundException("Weekly review of the week starting", week));
    }
}
