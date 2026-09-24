package com.luppol.lifebalance.application.goal;

import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalDetails;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.goal.GoalRepository;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRoleRepository;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import com.luppol.lifebalance.domain.value.CoreValueRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GoalCommandService implements GoalCommands {
    private final GoalRepository goals;
    private final LifeRoleRepository lifeRoles;
    private final CoreValueRepository coreValues;

    public GoalCommandService(GoalRepository goals, LifeRoleRepository lifeRoles, CoreValueRepository coreValues) {
        this.goals = goals;
        this.lifeRoles = lifeRoles;
        this.coreValues = coreValues;
    }

    @Override
    public void set(SetGoal command) {
        requireOwnedReferences(command.owner(), command.details());
        goals.add(Goal.set(command.id(), command.owner(), command.details()));
    }

    @Override
    public void revise(ReviseGoal command) {
        Goal goal = goals.findRequired(command.owner(), command.id());
        requireOwnedReferences(command.owner(), command.details());
        goals.update(goal.revisedTo(command.details()));
    }

    @Override
    public void achieve(PersonId owner, GoalId id) {
        goals.update(goals.findRequired(owner, id).achieved());
    }

    @Override
    public void drop(PersonId owner, GoalId id) {
        goals.update(goals.findRequired(owner, id).dropped());
    }

    @Override
    public void reopen(PersonId owner, GoalId id) {
        goals.update(goals.findRequired(owner, id).reopened());
    }

    @Override
    public void remove(PersonId owner, GoalId id) {
        goals.findRequired(owner, id);
        goals.remove(owner, id);
    }

    private void requireOwnedReferences(PersonId owner, GoalDetails details) {
        if (lifeRoles.findById(owner, details.roleId()).isEmpty()) {
            throw new RuleViolationException("Life role %s does not exist".formatted(details.roleId()));
        }
        Set<CoreValueId> known = coreValues.findAllByOwner(owner).stream()
                .map(CoreValue::id)
                .collect(Collectors.toSet());
        List<CoreValueId> unknown = details.valueIds().stream().filter(id -> !known.contains(id)).toList();
        if (!unknown.isEmpty()) {
            throw new RuleViolationException("Core values %s do not exist".formatted(unknown));
        }
    }
}
