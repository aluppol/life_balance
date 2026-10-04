package com.luppol.lifebalance.adapter.web.security;

import com.luppol.lifebalance.adapter.web.WebTest;
import com.luppol.lifebalance.domain.person.PersonAlreadyEnrolledException;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityTest extends WebTest {
    @Test
    void api_requiresAToken() throws Exception {
        mvc.perform(get("/api/values")).andExpect(status().isUnauthorized());
    }

    @Test
    void api_requiresAPlannerRole() throws Exception {
        mvc.perform(get("/api/values").with(withRoles("someone", "offline_access"))).andExpect(status().isForbidden());
    }

    @Test
    void api_admitsMembersAdministratorsAndGuests() throws Exception {
        mvc.perform(get("/api/values").with(member())).andExpect(status().isOk());
        mvc.perform(get("/api/values").with(withRoles("admin", "ADMIN"))).andExpect(status().isOk());
        mvc.perform(get("/api/values").with(guest())).andExpect(status().isOk());
    }

    @Test
    void firstRequest_enrollsAMember() throws Exception {
        when(personQueries.isEnrolled(OWNER)).thenReturn(false);

        mvc.perform(get("/api/values").with(member())).andExpect(status().isOk());

        verify(personCommands).enrollMember(OWNER);
        verify(personCommands, never()).enrollGuest(OWNER);
    }

    @Test
    void firstRequest_enrollsAGuestVisitWithTheDemoWorkspace() throws Exception {
        when(personQueries.isEnrolled(GUEST_VISIT)).thenReturn(false);

        mvc.perform(get("/api/values").with(guest())).andExpect(status().isOk());

        verify(personCommands).enrollGuest(GUEST_VISIT);
        verify(personCommands, never()).enrollMember(any());
        verify(coreValueQueries).listAll(GUEST_VISIT);
    }

    @Test
    void eachGuestSession_isASeparatePerson() throws Exception {
        when(personQueries.isEnrolled(any())).thenReturn(false);

        mvc.perform(get("/api/values").with(guest())).andExpect(status().isOk());
        mvc.perform(get("/api/values").with(withClaims(OWNER.value(), Map.of("sid", "next-session"), "guest")))
                .andExpect(status().isOk());

        verify(personCommands).enrollGuest(GUEST_VISIT);
        verify(personCommands).enrollGuest(new PersonId("owner-subject:next-session"));
    }

    @Test
    void guestTokenWithoutSession_isRejected() throws Exception {
        mvc.perform(get("/api/values").with(withRoles(OWNER.value(), "guest")))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", containsString("invalid_token")));
        mvc.perform(get("/api/me").with(withClaims(OWNER.value(), Map.of("sid", " "), "guest")))
                .andExpect(status().isUnauthorized());

        verify(personCommands, never()).enrollGuest(any());
        verify(coreValueQueries, never()).listAll(any());
    }

    @Test
    void memberWithASession_isStillKnownBySubject() throws Exception {
        when(personQueries.isEnrolled(OWNER)).thenReturn(false);

        mvc.perform(get("/api/values").with(withClaims(OWNER.value(), Map.of("sid", "member-session"), "USER")))
                .andExpect(status().isOk());

        verify(personCommands).enrollMember(OWNER);
        verify(coreValueQueries).listAll(OWNER);
    }

    @Test
    void enrolledPerson_isNotEnrolledAgain() throws Exception {
        mvc.perform(get("/api/values").with(member())).andExpect(status().isOk());

        verify(personCommands, never()).enrollMember(OWNER);
    }

    @Test
    void concurrentEnrollment_isNotAnError() throws Exception {
        when(personQueries.isEnrolled(OWNER)).thenReturn(false);
        doThrow(new PersonAlreadyEnrolledException(OWNER)).when(personCommands).enrollMember(OWNER);

        mvc.perform(get("/api/values").with(member())).andExpect(status().isOk());
    }

    @Test
    void crossSiteWrite_isRejected() throws Exception {
        mvc.perform(post("/api/values").with(member()).header("Sec-Fetch-Site", "cross-site")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Integrity\"}"))
                .andExpect(status().isForbidden());

        verify(coreValueCommands, never()).add(any());
    }

    @Test
    void crossSiteRead_isAllowed() throws Exception {
        mvc.perform(get("/api/values").with(member()).header("Sec-Fetch-Site", "cross-site"))
                .andExpect(status().isOk());
    }

    @Test
    void sameOriginWrite_isAllowed() throws Exception {
        when(coreValueQueries.find(any(), any()))
                .thenReturn(new CoreValue(CoreValueId.random(), OWNER, "Integrity", "", 0));

        mvc.perform(post("/api/values").with(member()).header("Sec-Fetch-Site", "same-origin")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Integrity\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void responses_carryBrowserProtections() throws Exception {
        mvc.perform(get("/api/values").with(member()))
                .andExpect(header().string("Content-Security-Policy", containsString("frame-ancestors 'none'")))
                .andExpect(header().string("Referrer-Policy", "no-referrer"))
                .andExpect(header().string("Permissions-Policy", containsString("camera=()")))
                .andExpect(header().string("Cross-Origin-Opener-Policy", "same-origin"))
                .andExpect(header().string("Cross-Origin-Resource-Policy", "same-origin"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }
}
