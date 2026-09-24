package com.luppol.lifebalance.application.demo;

import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalDetails;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.mission.MissionStatement;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.ActivityDetails;
import com.luppol.lifebalance.domain.planning.ActivityId;
import com.luppol.lifebalance.domain.planning.PlannedActivity;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.review.WeeklyReview;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DemoWorkspace {
    private final PlannerRepositories repositories;
    private final Clock clock;

    public DemoWorkspace(PlannerRepositories repositories, Clock clock) {
        this.repositories = repositories;
        this.clock = clock;
    }

    public void furnish(PersonId owner) {
        WeekStart thisWeek = WeekStart.containing(LocalDate.now(clock));
        repositories.missionStatements().save(new MissionStatement(owner, DemoContent.MISSION));
        Map<String, CoreValueId> values = addValues(owner);
        Map<String, LifeRoleId> roles = addRoles(owner);
        Map<String, GoalId> goals = addGoals(owner, thisWeek, roles, values);
        DemoReferences references = new DemoReferences(roles, goals);
        addActivities(owner, thisWeek, DemoContent.THIS_WEEK, references);
        addActivities(owner, thisWeek.previous(), DemoContent.LAST_WEEK, references);
        repositories.reviews().save(new WeeklyReview(owner, thisWeek.previous(), DemoContent.LAST_WEEK_ACCOMPLISHMENTS,
                DemoContent.LAST_WEEK_LESSONS, DemoContent.LAST_WEEK_RENEWAL));
    }

    private Map<String, CoreValueId> addValues(PersonId owner) {
        List<DemoValue> demoValues = DemoContent.VALUES;
        List<CoreValue> values = IntStream.range(0, demoValues.size())
                .mapToObj(position -> new CoreValue(CoreValueId.random(), owner, demoValues.get(position).name(),
                        demoValues.get(position).description(), position))
                .toList();
        values.forEach(repositories.coreValues()::add);
        return values.stream().collect(Collectors.toMap(CoreValue::name, CoreValue::id));
    }

    private Map<String, LifeRoleId> addRoles(PersonId owner) {
        List<DemoRole> demoRoles = DemoContent.ROLES;
        IntStream.range(0, demoRoles.size())
                .mapToObj(index -> LifeRole.personal(LifeRoleId.random(), owner, demoRoles.get(index).name(),
                        demoRoles.get(index).description(), index + 1))
                .forEach(repositories.lifeRoles()::add);
        return repositories.lifeRoles().findAllByOwner(owner).stream()
                .collect(Collectors.toMap(LifeRole::name, LifeRole::id));
    }

    private Map<String, GoalId> addGoals(PersonId owner, WeekStart thisWeek, Map<String, LifeRoleId> roles,
                                         Map<String, CoreValueId> values) {
        List<Goal> goals = DemoContent.GOALS.stream()
                .map(demoGoal -> goal(owner, thisWeek, demoGoal, roles, values))
                .toList();
        goals.forEach(repositories.goals()::add);
        return goals.stream().collect(Collectors.toMap(goal -> goal.details().title(), Goal::id));
    }

    private static Goal goal(PersonId owner, WeekStart thisWeek, DemoGoal demoGoal, Map<String, LifeRoleId> roles,
                             Map<String, CoreValueId> values) {
        GoalDetails details = new GoalDetails(
                roles.get(demoGoal.role()),
                demoGoal.title(),
                demoGoal.description(),
                demoGoal.dueInWeeks().map(weeks -> thisWeek.sunday().plusWeeks(weeks)),
                demoGoal.values().stream().map(values::get).collect(Collectors.toSet()));
        return new Goal(GoalId.random(), owner, details, demoGoal.status());
    }

    private void addActivities(PersonId owner, WeekStart week, List<DemoActivity> demoActivities,
                               DemoReferences references) {
        demoActivities.stream()
                .map(demoActivity -> activity(owner, week, demoActivity, references))
                .forEach(repositories.activities()::add);
    }

    private static PlannedActivity activity(PersonId owner, WeekStart week, DemoActivity demoActivity,
                                            DemoReferences references) {
        ActivityDetails details = new ActivityDetails(
                references.roles().get(demoActivity.role()),
                demoActivity.goal().map(references.goals()::get),
                demoActivity.title(),
                demoActivity.quadrant(),
                demoActivity.day().map(week::day));
        return new PlannedActivity(ActivityId.random(), owner, week, details, demoActivity.isCompleted());
    }

    private record DemoReferences(Map<String, LifeRoleId> roles, Map<String, GoalId> goals) {
    }
}
