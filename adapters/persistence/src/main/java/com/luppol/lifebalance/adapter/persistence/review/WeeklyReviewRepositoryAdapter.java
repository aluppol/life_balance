package com.luppol.lifebalance.adapter.persistence.review;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.review.WeeklyReview;
import com.luppol.lifebalance.domain.review.WeeklyReviewRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WeeklyReviewRepositoryAdapter implements WeeklyReviewRepository {
    private final WeeklyReviewJpaRepository reviews;

    public WeeklyReviewRepositoryAdapter(WeeklyReviewJpaRepository reviews) {
        this.reviews = reviews;
    }

    @Override
    public Optional<WeeklyReview> findByWeek(PersonId owner, WeekStart week) {
        return reviews.findById(new WeeklyReviewKey(owner.value(), week.monday())).map(WeeklyReviewEntity::toDomain);
    }

    @Override
    public void save(WeeklyReview review) {
        reviews.save(WeeklyReviewEntity.from(review));
    }
}
