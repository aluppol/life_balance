package com.luppol.lifebalance.adapter.persistence.review;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.review.RenewalDimension;
import com.luppol.lifebalance.domain.review.WeeklyReview;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "weekly_review")
public class WeeklyReviewEntity {
    @EmbeddedId
    private WeeklyReviewKey key;

    private String accomplishments;

    private String lessons;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "weekly_review_renewal", joinColumns = {
            @JoinColumn(name = "person_id", referencedColumnName = "person_id"),
            @JoinColumn(name = "week_start", referencedColumnName = "week_start")})
    @Enumerated(EnumType.STRING)
    @Column(name = "dimension")
    private Set<RenewalDimension> renewedDimensions = new HashSet<>();

    protected WeeklyReviewEntity() {
    }

    private WeeklyReviewEntity(WeeklyReview review) {
        this.key = new WeeklyReviewKey(review.owner().value(), review.week().monday());
        this.accomplishments = review.accomplishments();
        this.lessons = review.lessons();
        this.renewedDimensions = new HashSet<>(review.renewedDimensions());
    }

    static WeeklyReviewEntity from(WeeklyReview review) {
        return new WeeklyReviewEntity(review);
    }

    WeeklyReview toDomain() {
        Set<RenewalDimension> dimensions = EnumSet.noneOf(RenewalDimension.class);
        dimensions.addAll(renewedDimensions);
        return new WeeklyReview(new PersonId(key.personId()), new WeekStart(key.weekStart()), accomplishments, lessons,
                dimensions);
    }
}
