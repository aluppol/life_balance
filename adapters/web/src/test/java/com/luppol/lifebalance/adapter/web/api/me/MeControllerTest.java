package com.luppol.lifebalance.adapter.web.api.me;

import com.luppol.lifebalance.adapter.web.WebTest;
import com.luppol.lifebalance.adapter.web.security.RealmRolesConverter;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MeControllerTest extends WebTest {
    @Test
    void me_usesTheFullName() throws Exception {
        mvc.perform(get("/api/me").with(token(Map.of("name", "Alex Morgan", "preferred_username", "alex"), "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Alex Morgan"))
                .andExpect(jsonPath("$.isGuest").value(false));
    }

    @Test
    void me_fallsBackToTheUsernameForGuests() throws Exception {
        mvc.perform(get("/api/me").with(token(Map.of("name", " ", "preferred_username", "recruiter"), "guest")))
                .andExpect(jsonPath("$.displayName").value("recruiter"))
                .andExpect(jsonPath("$.isGuest").value(true));
    }

    @Test
    void me_fallsBackToTheSubject() throws Exception {
        mvc.perform(get("/api/me").with(member()))
                .andExpect(jsonPath("$.displayName").value(OWNER.value()));
    }

    private static RequestPostProcessor token(Map<String, String> names, String role) {
        return jwt().jwt(token -> token.subject(OWNER.value())
                        .claims(claims -> claims.putAll(names))
                        .claim("realm_access", Map.of("roles", List.of(role))))
                .authorities(new RealmRolesConverter());
    }
}
