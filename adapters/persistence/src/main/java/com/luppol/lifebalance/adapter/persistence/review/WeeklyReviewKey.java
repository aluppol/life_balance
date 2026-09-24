package com.luppol.lifebalance.adapter.persistence.review;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public record WeeklyReviewKey(@Column(name = "person_id") String personId,
                              @Column(name = "week_start") LocalDate weekStart) {
}
