package com.luppol.lifebalance;

import com.luppol.lifebalance.application.person.PersonQueries;
import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.person.PersonId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
class InactiveMembersIT extends IntegrationTest {
    private final PersonId idleMember = new PersonId("idle-" + UUID.randomUUID());
    private final PersonId activeMember = new PersonId("active-" + UUID.randomUUID());
    private final String guestAccount = "guest-" + UUID.randomUUID();
    private final PersonId guestVisit = new PersonId(guestAccount + ":session");
    private final RequestPostProcessor idle = person(idleMember.value(), "USER");

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private PersonQueries personQueries;

    @Test
    void apiRequest_makesAnIdleMemberActiveAgain() throws Exception {
        mvc.perform(get("/api/me").with(idle)).andExpect(status().isOk());
        makeIdleFor(idleMember, 400);

        mvc.perform(get("/api/me").with(idle)).andExpect(status().isOk());

        assertThat(jdbc.queryForObject("select last_active_at >= date_trunc('day', clock_timestamp()) "
                + "from core.person where id = ?", Boolean.class, idleMember.value())).isTrue();
    }

    @Test
    void inactiveMembersCommand_listsMembersIdleForTheGivenDays(CapturedOutput output) throws Exception {
        enrollIdleAndActivePeople();

        LifeBalanceApplication.main(operatorArguments(InactiveMembers.NAME, "365"));

        assertThat(output.getOut())
                .containsPattern("Members: \\d+")
                .containsPattern("Inactive for at least 365 days: \\d+")
                .containsPattern(idleMember.value() + "  last active \\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z")
                .doesNotContain(activeMember.value())
                .doesNotContain(guestAccount);
        assertThat(personQueries.isEnrolled(idleMember)).isTrue();
    }

    @Test
    void removeInactiveMembersCommand_removesIdleMembersAndSparesGuestsAndActiveMembers(CapturedOutput output)
            throws Exception {
        enrollIdleAndActivePeople();
        mvc.perform(put("/api/mission").with(idle).contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"Mine\"}")).andExpect(status().isOk());
        makeIdleFor(idleMember, 400);

        LifeBalanceApplication.main(operatorArguments(RemoveInactiveMembers.NAME, "365"));

        assertThat(output.getOut()).contains(idleMember.value() + "  last active ")
                .containsPattern("Removed members inactive for at least 365 days; members now: \\d+");
        assertThat(personQueries.isEnrolled(idleMember)).isFalse();
        assertThat(personQueries.isEnrolled(activeMember)).isTrue();
        assertThat(personQueries.isEnrolled(guestVisit)).isTrue();
        mvc.perform(get("/api/mission").with(idle)).andExpect(status().isNotFound());
    }

    @Test
    void commands_refuseAMissingOrMalformedPeriodBeforeStarting() {
        assertThatThrownBy(() -> LifeBalanceApplication.main(new String[]{InactiveMembers.NAME}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usage: inactive-members <days>");
        assertThatThrownBy(() -> LifeBalanceApplication.main(new String[]{RemoveInactiveMembers.NAME, "a-year"}))
                .hasMessage("Usage: remove-inactive-members <days>");
        assertThatThrownBy(() -> LifeBalanceApplication.main(new String[]{RemoveInactiveMembers.NAME, "365", "30"}))
                .hasMessage("Usage: remove-inactive-members <days>");
        assertThatThrownBy(() -> LifeBalanceApplication.main(new String[]{RemoveInactiveMembers.NAME, "0"}))
                .isInstanceOf(RuleViolationException.class)
                .hasMessage("Inactivity period must be positive");
    }

    private void enrollIdleAndActivePeople() throws Exception {
        mvc.perform(get("/api/me").with(idle)).andExpect(status().isOk());
        mvc.perform(get("/api/me").with(person(activeMember.value(), "USER"))).andExpect(status().isOk());
        mvc.perform(get("/api/me").with(guestVisit(guestAccount, "session"))).andExpect(status().isOk());
        makeIdleFor(idleMember, 400);
        makeIdleFor(activeMember, 364);
        makeIdleFor(guestVisit, 400);
    }

    private void makeIdleFor(PersonId person, int days) {
        jdbc.update("update core.person set last_active_at = clock_timestamp() - make_interval(days => ?) where id = ?",
                days, person.value());
    }
}
