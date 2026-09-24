package com.luppol.lifebalance.application.review;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.review.WeeklyReview;

public interface WeeklyReviewQueries {
    WeeklyReview find(PersonId owner, WeekStart week);
}
