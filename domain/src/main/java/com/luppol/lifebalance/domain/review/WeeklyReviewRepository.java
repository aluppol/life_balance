package com.luppol.lifebalance.domain.review;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.WeekStart;

import java.util.Optional;

public interface WeeklyReviewRepository {
    Optional<WeeklyReview> findByWeek(PersonId owner, WeekStart week);

    void save(WeeklyReview review);
}
