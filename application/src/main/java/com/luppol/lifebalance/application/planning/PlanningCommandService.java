package com.luppol.lifebalance.application.planning;

import com.luppol.lifebalance.domain.Invariants;
import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.goal.GoalRepository;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.ActivityDetails;
import com.luppol.lifebalance.domain.planning.ActivityId;
import com.luppol.lifebalance.domain.planning.PlannedActivity;
import com.luppol.lifebalance.domain.planning.PlannedActivityRepository;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.role.LifeRoleRepository;

public class PlanningCommandService implements PlanningCommands {
    private final PlannedActivityRepository activities;
    private final LifeRoleRepository lifeRoles;
    private final GoalRepository goals;

    public PlanningCommandService(PlannedActivityRepository activities, LifeRoleRepository lifeRoles,
                                  GoalRepository goals) {
        this.activities = activities;
        this.lifeRoles = lifeRoles;
        this.goals = goals;
    }

    @Override
    public void plan(PlanActivity command) {
        int plannedThisWeek = activities.findAllInWeek(command.owner(), command.week()).size();
        Invariants.requireRoomForOneMore(plannedThisWeek, PlannedActivity.MAXIMUM_PER_WEEK, "activities in a week");
        requireOwnedReferences(command.owner(), command.details());
        activities.add(PlannedActivity.planned(command.id(), command.owner(), command.week(), command.details()));
    }

    @Override
    public void revise(ReviseActivity command) {
        PlannedActivity activity = activities.findRequired(command.owner(), command.id());
        requireOwnedReferences(command.owner(), command.details());
        activities.update(activity.revisedTo(command.details()));
    }

    @Override
    public void complete(PersonId owner, ActivityId id) {
        activities.update(activities.findRequired(owner, id).completed());
    }

    @Override
    public void reopen(PersonId owner, ActivityId id) {
        activities.update(activities.findRequired(owner, id).reopened());
    }

    @Override
    public void remove(PersonId owner, ActivityId id) {
        activities.findRequired(owner, id);
        activities.remove(owner, id);
    }

    private void requireOwnedReferences(PersonId owner, ActivityDetails details) {
        if (lifeRoles.findById(owner, details.roleId()).isEmpty()) {
            throw new RuleViolationException("Life role %s does not exist".formatted(details.roleId()));
        }
        details.goalId().ifPresent(goalId -> requireGoalOfRole(owner, goalId, details.roleId()));
    }

    private void requireGoalOfRole(PersonId owner, GoalId goalId, LifeRoleId roleId) {
        Goal goal = goals.findById(owner, goalId)
                .orElseThrow(() -> new RuleViolationException("Goal %s does not exist".formatted(goalId)));
        if (!goal.details().roleId().equals(roleId)) {
            throw new RuleViolationException("Goal '%s' belongs to another role".formatted(goal.details().title()));
        }
    }
}
