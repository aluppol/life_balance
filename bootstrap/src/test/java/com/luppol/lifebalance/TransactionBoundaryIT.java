package com.luppol.lifebalance;

import com.luppol.lifebalance.application.demo.DemoWorkspace;
import com.luppol.lifebalance.application.person.PersonCommands;
import com.luppol.lifebalance.application.person.PersonQueries;
import com.luppol.lifebalance.application.value.AddCoreValue;
import com.luppol.lifebalance.application.value.CoreValueCommandService;
import com.luppol.lifebalance.application.value.CoreValueQueryService;
import com.luppol.lifebalance.domain.person.PersonId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Clock;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class TransactionBoundaryIT extends IntegrationTest {
    @MockitoBean
    private Clock brokenClock;

    @Autowired
    private PersonCommands personCommands;

    @Autowired
    private PersonQueries personQueries;

    @Test
    void failedCommand_leavesNothingBehind() {
        when(brokenClock.instant()).thenThrow(new IllegalStateException("The clock stopped"));
        PersonId guest = new PersonId("guest-" + UUID.randomUUID());

        assertThatThrownBy(() -> personCommands.enrollGuest(guest)).hasMessage("The clock stopped");

        assertThat(personQueries.isEnrolled(guest)).isFalse();
    }

    @Test
    void successfulCommand_commits() {
        PersonId member = new PersonId("member-" + UUID.randomUUID());

        personCommands.enrollMember(member);

        assertThat(personQueries.isEnrolled(member)).isTrue();
    }

    @Test
    void transactionAttributes_followTheServiceName() throws NoSuchMethodException {
        ApplicationServiceTransactions attributes = new ApplicationServiceTransactions();
        Class<?> commandService = CoreValueCommandService.class;
        Class<?> queryService = CoreValueQueryService.class;

        assertThat(attributes.getTransactionAttribute(commandService.getMethod("add", AddCoreValue.class), commandService).isReadOnly()).isFalse();
        assertThat(attributes.getTransactionAttribute(queryService.getMethod("listAll", PersonId.class), queryService)
                .isReadOnly()).isTrue();
        assertThat(attributes.getTransactionAttribute(Object.class.getMethod("toString"), String.class)).isNull();
        assertThat(attributes.getTransactionAttribute(Object.class.getMethod("toString"), null)).isNull();
        assertThat(attributes.getTransactionAttribute(Object.class.getMethod("toString"), DemoWorkspace.class)).isNull();
    }
}
