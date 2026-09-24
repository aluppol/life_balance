package com.luppol.lifebalance.adapter.persistence.goal;

import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalDetails;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.goal.GoalStatus;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.value.CoreValueId;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "goal")
public class GoalEntity {
    @Id
    private UUID id;

    @Column(name = "person_id")
    private String personId;

    @Column(name = "life_role_id")
    private UUID lifeRoleId;

    private String title;

    private String description;

    @Column(name = "due_on")
    private LocalDate dueOn;

    @Enumerated(EnumType.STRING)
    private GoalStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "goal_core_value", joinColumns = @JoinColumn(name = "goal_id"))
    private Set<GoalCoreValue> coreValues = new HashSet<>();

    protected GoalEntity() {
    }

    private GoalEntity(Goal goal) {
        GoalDetails details = goal.details();
        this.id = goal.id().value();
        this.personId = goal.owner().value();
        this.lifeRoleId = details.roleId().value();
        this.title = details.title();
        this.description = details.description();
        this.dueOn = details.dueOn().orElse(null);
        this.status = goal.status();
        this.coreValues = details.valueIds().stream()
                .map(valueId -> new GoalCoreValue(personId, valueId.value()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    static GoalEntity from(Goal goal) {
        return new GoalEntity(goal);
    }

    Goal toDomain() {
        Set<CoreValueId> valueIds = coreValues.stream()
                .map(link -> new CoreValueId(link.coreValueId()))
                .collect(Collectors.toSet());
        GoalDetails details = new GoalDetails(new LifeRoleId(lifeRoleId), title, description,
                Optional.ofNullable(dueOn), valueIds);
        return new Goal(new GoalId(id), new PersonId(personId), details, status);
    }
}
