package com.luppol.lifebalance.adapter.persistence;

import com.luppol.lifebalance.adapter.persistence.goal.GoalRepositoryAdapter;
import com.luppol.lifebalance.adapter.persistence.mission.MissionStatementRepositoryAdapter;
import com.luppol.lifebalance.adapter.persistence.person.PersonRepositoryAdapter;
import com.luppol.lifebalance.adapter.persistence.planning.PlannedActivityRepositoryAdapter;
import com.luppol.lifebalance.adapter.persistence.review.WeeklyReviewRepositoryAdapter;
import com.luppol.lifebalance.adapter.persistence.role.LifeRoleRepositoryAdapter;
import com.luppol.lifebalance.adapter.persistence.value.CoreValueRepositoryAdapter;
import com.luppol.lifebalance.domain.goal.GoalRepository;
import com.luppol.lifebalance.domain.mission.MissionStatementRepository;
import com.luppol.lifebalance.domain.person.Person;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.person.PersonRepository;
import com.luppol.lifebalance.domain.planning.PlannedActivityRepository;
import com.luppol.lifebalance.domain.review.WeeklyReviewRepository;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.role.LifeRoleRepository;
import com.luppol.lifebalance.domain.value.CoreValueRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        PersonRepositoryAdapter.class,
        MissionStatementRepositoryAdapter.class,
        CoreValueRepositoryAdapter.class,
        LifeRoleRepositoryAdapter.class,
        GoalRepositoryAdapter.class,
        PlannedActivityRepositoryAdapter.class,
        WeeklyReviewRepositoryAdapter.class})
public abstract class PersistenceTest {
    protected static final PersonId OWNER = new PersonId("owner");
    protected static final PersonId STRANGER = new PersonId("stranger");

    private static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:16-alpine").withInitScript("db/create-schema.sql");

    static {
        POSTGRES.start();
    }

    @Autowired
    protected PersonRepository people;

    @Autowired
    protected MissionStatementRepository missionStatements;

    @Autowired
    protected CoreValueRepository coreValues;

    @Autowired
    protected LifeRoleRepository lifeRoles;

    @Autowired
    protected GoalRepository goals;

    @Autowired
    protected PlannedActivityRepository activities;

    @Autowired
    protected WeeklyReviewRepository reviews;

    @Autowired
    protected EntityManager entityManager;

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    protected void enroll(PersonId... ids) {
        for (PersonId id : ids) {
            people.enroll(Person.member(id));
        }
    }

    protected LifeRole addRole(PersonId owner, String name) {
        LifeRole role = LifeRole.personal(LifeRoleId.random(), owner, name, "", 1);
        lifeRoles.add(role);
        return role;
    }

    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
