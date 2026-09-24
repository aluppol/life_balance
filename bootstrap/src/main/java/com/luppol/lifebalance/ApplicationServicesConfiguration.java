package com.luppol.lifebalance;

import com.luppol.lifebalance.application.demo.DemoWorkspace;
import com.luppol.lifebalance.application.demo.PlannerRepositories;
import com.luppol.lifebalance.application.goal.GoalCommandService;
import com.luppol.lifebalance.application.goal.GoalCommands;
import com.luppol.lifebalance.application.goal.GoalQueries;
import com.luppol.lifebalance.application.goal.GoalQueryService;
import com.luppol.lifebalance.application.mission.MissionStatementCommandService;
import com.luppol.lifebalance.application.mission.MissionStatementCommands;
import com.luppol.lifebalance.application.mission.MissionStatementQueries;
import com.luppol.lifebalance.application.mission.MissionStatementQueryService;
import com.luppol.lifebalance.application.person.PersonCommandService;
import com.luppol.lifebalance.application.person.PersonCommands;
import com.luppol.lifebalance.application.person.PersonQueries;
import com.luppol.lifebalance.application.person.PersonQueryService;
import com.luppol.lifebalance.application.planning.PlanningCommandService;
import com.luppol.lifebalance.application.planning.PlanningCommands;
import com.luppol.lifebalance.application.planning.PlanningQueries;
import com.luppol.lifebalance.application.planning.PlanningQueryService;
import com.luppol.lifebalance.application.review.WeeklyReviewCommandService;
import com.luppol.lifebalance.application.review.WeeklyReviewCommands;
import com.luppol.lifebalance.application.review.WeeklyReviewQueries;
import com.luppol.lifebalance.application.review.WeeklyReviewQueryService;
import com.luppol.lifebalance.application.role.LifeRoleCommandService;
import com.luppol.lifebalance.application.role.LifeRoleCommands;
import com.luppol.lifebalance.application.role.LifeRoleQueries;
import com.luppol.lifebalance.application.role.LifeRoleQueryService;
import com.luppol.lifebalance.application.value.CoreValueCommandService;
import com.luppol.lifebalance.application.value.CoreValueCommands;
import com.luppol.lifebalance.application.value.CoreValueQueries;
import com.luppol.lifebalance.application.value.CoreValueQueryService;
import com.luppol.lifebalance.domain.goal.GoalRepository;
import com.luppol.lifebalance.domain.mission.MissionStatementRepository;
import com.luppol.lifebalance.domain.person.PersonRepository;
import com.luppol.lifebalance.domain.planning.PlannedActivityRepository;
import com.luppol.lifebalance.domain.review.WeeklyReviewRepository;
import com.luppol.lifebalance.domain.role.LifeRoleRepository;
import com.luppol.lifebalance.domain.value.CoreValueRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration(proxyBeanMethods = false)
public class ApplicationServicesConfiguration {
    @Bean
    Clock clock(@Value("${lifebalance.demo.time-zone}") ZoneId timeZone) {
        return Clock.system(timeZone);
    }

    @Bean
    PlannerRepositories plannerRepositories(MissionStatementRepository missionStatements, CoreValueRepository coreValues,
                                            LifeRoleRepository lifeRoles, GoalRepository goals,
                                            PlannedActivityRepository activities, WeeklyReviewRepository reviews) {
        return new PlannerRepositories(missionStatements, coreValues, lifeRoles, goals, activities, reviews);
    }

    @Bean
    DemoWorkspace demoWorkspace(PlannerRepositories repositories, Clock clock) {
        return new DemoWorkspace(repositories, clock);
    }

    @Bean
    PersonCommands personCommands(PersonRepository people, LifeRoleRepository lifeRoles, DemoWorkspace demoWorkspace) {
        return new PersonCommandService(people, lifeRoles, demoWorkspace);
    }

    @Bean
    PersonQueries personQueries(PersonRepository people) {
        return new PersonQueryService(people);
    }

    @Bean
    MissionStatementCommands missionStatementCommands(MissionStatementRepository missionStatements) {
        return new MissionStatementCommandService(missionStatements);
    }

    @Bean
    MissionStatementQueries missionStatementQueries(MissionStatementRepository missionStatements) {
        return new MissionStatementQueryService(missionStatements);
    }

    @Bean
    CoreValueCommands coreValueCommands(CoreValueRepository coreValues) {
        return new CoreValueCommandService(coreValues);
    }

    @Bean
    CoreValueQueries coreValueQueries(CoreValueRepository coreValues) {
        return new CoreValueQueryService(coreValues);
    }

    @Bean
    LifeRoleCommands lifeRoleCommands(LifeRoleRepository lifeRoles) {
        return new LifeRoleCommandService(lifeRoles);
    }

    @Bean
    LifeRoleQueries lifeRoleQueries(LifeRoleRepository lifeRoles) {
        return new LifeRoleQueryService(lifeRoles);
    }

    @Bean
    GoalCommands goalCommands(GoalRepository goals, LifeRoleRepository lifeRoles, CoreValueRepository coreValues) {
        return new GoalCommandService(goals, lifeRoles, coreValues);
    }

    @Bean
    GoalQueries goalQueries(GoalRepository goals) {
        return new GoalQueryService(goals);
    }

    @Bean
    PlanningCommands planningCommands(PlannedActivityRepository activities, LifeRoleRepository lifeRoles,
                                      GoalRepository goals) {
        return new PlanningCommandService(activities, lifeRoles, goals);
    }

    @Bean
    PlanningQueries planningQueries(PlannedActivityRepository activities) {
        return new PlanningQueryService(activities);
    }

    @Bean
    WeeklyReviewCommands weeklyReviewCommands(WeeklyReviewRepository reviews) {
        return new WeeklyReviewCommandService(reviews);
    }

    @Bean
    WeeklyReviewQueries weeklyReviewQueries(WeeklyReviewRepository reviews) {
        return new WeeklyReviewQueryService(reviews);
    }
}
