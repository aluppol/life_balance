package com.luppol.lifebalance.application.demo;

import com.luppol.lifebalance.domain.goal.GoalRepository;
import com.luppol.lifebalance.domain.mission.MissionStatementRepository;
import com.luppol.lifebalance.domain.planning.PlannedActivityRepository;
import com.luppol.lifebalance.domain.review.WeeklyReviewRepository;
import com.luppol.lifebalance.domain.role.LifeRoleRepository;
import com.luppol.lifebalance.domain.value.CoreValueRepository;

public record PlannerRepositories(
        MissionStatementRepository missionStatements,
        CoreValueRepository coreValues,
        LifeRoleRepository lifeRoles,
        GoalRepository goals,
        PlannedActivityRepository activities,
        WeeklyReviewRepository reviews) {
}
