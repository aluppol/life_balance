package com.luppol.lifebalance.adapter.persistence.planning;

import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.ActivityDetails;
import com.luppol.lifebalance.domain.planning.ActivityId;
import com.luppol.lifebalance.domain.planning.PlannedActivity;
import com.luppol.lifebalance.domain.planning.Quadrant;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "planned_activity")
public class PlannedActivityEntity {
    @Id
    private UUID id;

    @Column(name = "person_id")
    private String personId;

    @Column(name = "week_start")
    private LocalDate weekStart;

    @Column(name = "life_role_id")
    private UUID lifeRoleId;

    @Column(name = "goal_id")
    private UUID goalId;

    private String title;

    @Enumerated(EnumType.STRING)
    private Quadrant quadrant;

    @Column(name = "scheduled_on")
    private LocalDate scheduledOn;

    @Column(name = "is_completed")
    private boolean isCompleted;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    protected PlannedActivityEntity() {
    }

    private PlannedActivityEntity(PlannedActivity activity) {
        ActivityDetails details = activity.details();
        this.id = activity.id().value();
        this.personId = activity.owner().value();
        this.weekStart = activity.week().monday();
        this.lifeRoleId = details.roleId().value();
        this.goalId = details.goalId().map(GoalId::value).orElse(null);
        this.title = details.title();
        this.quadrant = details.quadrant();
        this.scheduledOn = details.scheduledOn().orElse(null);
        this.isCompleted = activity.isCompleted();
    }

    static PlannedActivityEntity from(PlannedActivity activity) {
        return new PlannedActivityEntity(activity);
    }

    PlannedActivity toDomain() {
        ActivityDetails details = new ActivityDetails(new LifeRoleId(lifeRoleId),
                Optional.ofNullable(goalId).map(GoalId::new), title, quadrant, Optional.ofNullable(scheduledOn));
        return new PlannedActivity(new ActivityId(id), new PersonId(personId), new WeekStart(weekStart), details,
                isCompleted);
    }
}
