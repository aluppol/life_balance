package com.luppol.lifebalance.adapter.web.security;

import com.luppol.lifebalance.adapter.web.WebTest;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ActivityInterceptorTest extends WebTest {
    @Test
    void apiRequest_recordsTheMembersActivity() throws Exception {
        mvc.perform(get("/api/values").with(member())).andExpect(status().isOk());

        verify(personCommands).recordActivity(OWNER);
    }

    @Test
    void apiRequest_recordsTheGuestVisitsActivity() throws Exception {
        mvc.perform(get("/api/me").with(guest())).andExpect(status().isOk());

        verify(personCommands).recordActivity(GUEST_VISIT);
    }

    @Test
    void firstRequest_recordsActivityAfterTheEnrollment() throws Exception {
        when(personQueries.isEnrolled(OWNER)).thenReturn(false);

        mvc.perform(get("/api/values").with(member())).andExpect(status().isOk());

        InOrder order = inOrder(personCommands, coreValueQueries);
        order.verify(personCommands).enrollMember(OWNER);
        order.verify(personCommands).recordActivity(OWNER);
        order.verify(coreValueQueries).listAll(OWNER);
    }

    @Test
    void unauthenticatedRequest_recordsNothing() throws Exception {
        mvc.perform(get("/api/values")).andExpect(status().isUnauthorized());

        verify(personCommands, never()).recordActivity(any());
    }

    @Test
    void applicationShell_recordsNothing() throws Exception {
        mvc.perform(get("/week/2026-09-21").with(member())).andExpect(status().isOk());

        verify(personCommands, never()).recordActivity(any());
    }
}
