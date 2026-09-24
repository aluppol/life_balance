package com.luppol.lifebalance.application.fakes;

import com.luppol.lifebalance.application.demo.PlannerRepositories;
import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.goal.GoalRepository;
import com.luppol.lifebalance.domain.mission.MissionStatement;
import com.luppol.lifebalance.domain.mission.MissionStatementRepository;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.ActivityId;
import com.luppol.lifebalance.domain.planning.PlannedActivity;
import com.luppol.lifebalance.domain.planning.PlannedActivityRepository;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.review.WeeklyReview;
import com.luppol.lifebalance.domain.review.WeeklyReviewRepository;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.role.LifeRoleRepository;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import com.luppol.lifebalance.domain.value.CoreValueRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlannerStore {
    public final OwnedRecords<CoreValueId, CoreValue> coreValueRecords =
            new OwnedRecords<>(CoreValue::id, CoreValue::owner);
    public final OwnedRecords<LifeRoleId, LifeRole> lifeRoleRecords = new OwnedRecords<>(LifeRole::id, LifeRole::owner);
    public final OwnedRecords<GoalId, Goal> goalRecords = new OwnedRecords<>(Goal::id, Goal::owner);
    public final OwnedRecords<ActivityId, PlannedActivity> activityRecords =
            new OwnedRecords<>(PlannedActivity::id, PlannedActivity::owner);
    public final Map<PersonId, MissionStatement> missionStatementRecords = new HashMap<>();
    public final Map<List<Object>, WeeklyReview> reviewRecords = new HashMap<>();

    public PlannerRepositories repositories() {
        return new PlannerRepositories(missionStatements(), coreValues(), lifeRoles(), goals(), activities(), reviews());
    }

    public void removeEverythingOf(PersonId owner) {
        coreValueRecords.removeAllOf(owner);
        lifeRoleRecords.removeAllOf(owner);
        goalRecords.removeAllOf(owner);
        activityRecords.removeAllOf(owner);
        missionStatementRecords.remove(owner);
        reviewRecords.keySet().removeIf(key -> key.getFirst().equals(owner));
    }

    public MissionStatementRepository missionStatements() {
        return new InMemoryMissionStatements();
    }

    public CoreValueRepository coreValues() {
        return new InMemoryCoreValues();
    }

    public LifeRoleRepository lifeRoles() {
        return new InMemoryLifeRoles();
    }

    public GoalRepository goals() {
        return new InMemoryGoals();
    }

    public PlannedActivityRepository activities() {
        return new InMemoryActivities();
    }

    public WeeklyReviewRepository reviews() {
        return new InMemoryReviews();
    }

    private class InMemoryMissionStatements implements MissionStatementRepository {
        @Override
        public Optional<MissionStatement> findByOwner(PersonId owner) {
            return Optional.ofNullable(missionStatementRecords.get(owner));
        }

        @Override
        public void save(MissionStatement statement) {
            missionStatementRecords.put(statement.owner(), statement);
        }
    }

    private class InMemoryCoreValues implements CoreValueRepository {
        @Override
        public List<CoreValue> findAllByOwner(PersonId owner) {
            return coreValueRecords.findAllByOwner(owner, Comparator.comparingInt(CoreValue::position));
        }

        @Override
        public Optional<CoreValue> findById(PersonId owner, CoreValueId id) {
            return coreValueRecords.findById(owner, id);
        }

        @Override
        public void add(CoreValue value) {
            coreValueRecords.put(value);
        }

        @Override
        public void update(CoreValue value) {
            coreValueRecords.put(value);
        }

        @Override
        public void updateAll(List<CoreValue> values) {
            values.forEach(coreValueRecords::put);
        }

        @Override
        public void remove(PersonId owner, CoreValueId id) {
            coreValueRecords.remove(owner, id);
        }
    }

    private class InMemoryLifeRoles implements LifeRoleRepository {
        @Override
        public List<LifeRole> findAllByOwner(PersonId owner) {
            return lifeRoleRecords.findAllByOwner(owner, Comparator.comparingInt(LifeRole::position));
        }

        @Override
        public Optional<LifeRole> findById(PersonId owner, LifeRoleId id) {
            return lifeRoleRecords.findById(owner, id);
        }

        @Override
        public boolean isInUse(PersonId owner, LifeRoleId id) {
            return goalRecords.all().stream().anyMatch(goal -> goal.details().roleId().equals(id))
                    || activityRecords.all().stream().anyMatch(activity -> activity.details().roleId().equals(id));
        }

        @Override
        public void add(LifeRole role) {
            lifeRoleRecords.put(role);
        }

        @Override
        public void update(LifeRole role) {
            lifeRoleRecords.put(role);
        }

        @Override
        public void updateAll(List<LifeRole> roles) {
            roles.forEach(lifeRoleRecords::put);
        }

        @Override
        public void remove(PersonId owner, LifeRoleId id) {
            lifeRoleRecords.remove(owner, id);
        }
    }

    private class InMemoryGoals implements GoalRepository {
        @Override
        public List<Goal> findAllByOwner(PersonId owner) {
            return goalRecords.findAllByOwner(owner, Comparator.comparing(goal -> goal.details().title()));
        }

        @Override
        public Optional<Goal> findById(PersonId owner, GoalId id) {
            return goalRecords.findById(owner, id);
        }

        @Override
        public void add(Goal goal) {
            goalRecords.put(goal);
        }

        @Override
        public void update(Goal goal) {
            goalRecords.put(goal);
        }

        @Override
        public void remove(PersonId owner, GoalId id) {
            goalRecords.remove(owner, id);
        }
    }

    private class InMemoryActivities implements PlannedActivityRepository {
        @Override
        public List<PlannedActivity> findAllInWeek(PersonId owner, WeekStart week) {
            return activityRecords.findAllByOwner(owner, Comparator.comparing(activity -> activity.details().title()))
                    .stream()
                    .filter(activity -> activity.week().equals(week))
                    .toList();
        }

        @Override
        public Optional<PlannedActivity> findById(PersonId owner, ActivityId id) {
            return activityRecords.findById(owner, id);
        }

        @Override
        public void add(PlannedActivity activity) {
            activityRecords.put(activity);
        }

        @Override
        public void update(PlannedActivity activity) {
            activityRecords.put(activity);
        }

        @Override
        public void remove(PersonId owner, ActivityId id) {
            activityRecords.remove(owner, id);
        }
    }

    private class InMemoryReviews implements WeeklyReviewRepository {
        @Override
        public Optional<WeeklyReview> findByWeek(PersonId owner, WeekStart week) {
            return Optional.ofNullable(reviewRecords.get(List.of(owner, week)));
        }

        @Override
        public void save(WeeklyReview review) {
            reviewRecords.put(List.of(review.owner(), review.week()), review);
        }
    }
}
