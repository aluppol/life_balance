package com.luppol.lifebalance.adapter.web;

import com.luppol.lifebalance.adapter.web.security.RealmRolesConverter;
import com.luppol.lifebalance.adapter.web.security.SecurityConfiguration;
import com.luppol.lifebalance.application.goal.GoalCommands;
import com.luppol.lifebalance.application.goal.GoalQueries;
import com.luppol.lifebalance.application.mission.MissionStatementCommands;
import com.luppol.lifebalance.application.mission.MissionStatementQueries;
import com.luppol.lifebalance.application.person.PersonCommands;
import com.luppol.lifebalance.application.person.PersonQueries;
import com.luppol.lifebalance.application.planning.PlanningCommands;
import com.luppol.lifebalance.application.planning.PlanningQueries;
import com.luppol.lifebalance.application.review.WeeklyReviewCommands;
import com.luppol.lifebalance.application.review.WeeklyReviewQueries;
import com.luppol.lifebalance.application.role.LifeRoleCommands;
import com.luppol.lifebalance.application.role.LifeRoleQueries;
import com.luppol.lifebalance.application.value.CoreValueCommands;
import com.luppol.lifebalance.application.value.CoreValueQueries;
import com.luppol.lifebalance.domain.person.PersonId;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest
@Import(SecurityConfiguration.class)
@TestPropertySource(properties = "lifebalance.security.access-token-header=X-Forwarded-Access-Token")
public abstract class WebTest {
    protected static final PersonId OWNER = new PersonId("owner-subject");

    @Autowired
    protected MockMvc mvc;

    @MockitoBean
    protected JwtDecoder jwtDecoder;

    @MockitoBean
    protected PersonQueries personQueries;

    @MockitoBean
    protected PersonCommands personCommands;

    @MockitoBean
    protected MissionStatementCommands missionStatementCommands;

    @MockitoBean
    protected MissionStatementQueries missionStatementQueries;

    @MockitoBean
    protected CoreValueCommands coreValueCommands;

    @MockitoBean
    protected CoreValueQueries coreValueQueries;

    @MockitoBean
    protected LifeRoleCommands lifeRoleCommands;

    @MockitoBean
    protected LifeRoleQueries lifeRoleQueries;

    @MockitoBean
    protected GoalCommands goalCommands;

    @MockitoBean
    protected GoalQueries goalQueries;

    @MockitoBean
    protected PlanningCommands planningCommands;

    @MockitoBean
    protected PlanningQueries planningQueries;

    @MockitoBean
    protected WeeklyReviewCommands weeklyReviewCommands;

    @MockitoBean
    protected WeeklyReviewQueries weeklyReviewQueries;

    @BeforeEach
    void everyoneIsEnrolled() {
        when(personQueries.isEnrolled(any())).thenReturn(true);
    }

    protected static RequestPostProcessor member() {
        return withRoles(OWNER.value(), "USER");
    }

    protected static RequestPostProcessor guest() {
        return withRoles(OWNER.value(), "guest");
    }

    protected static RequestPostProcessor withRoles(String subject, String... roles) {
        return jwt()
                .jwt(token -> token.subject(subject).claim("realm_access", Map.of("roles", List.of(roles))))
                .authorities(new RealmRolesConverter());
    }
}
