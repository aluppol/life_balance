package com.luppol.lifebalance;

import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccessTokenIT extends IntegrationTest {
    private static final String HEADER = "X-Forwarded-Access-Token";
    private static final String SUBJECT = "4c8b3a50-7a13-4c49-9d1f-3f5b0c1f1a11";

    @Test
    void validToken_isAccepted() throws Exception {
        mvc.perform(get("/api/me").header(HEADER, TestAccessTokens.token(SUBJECT, List.of("USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value(SUBJECT))
                .andExpect(jsonPath("$.isGuest").value(false));
    }

    @Test
    void tokenInTheAuthorizationHeader_isIgnored() throws Exception {
        mvc.perform(get("/api/me").header("Authorization", "Bearer " + TestAccessTokens.token(SUBJECT, List.of("USER"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenForAnotherAudience_isRejected() throws Exception {
        JWTClaimsSet claims = TestAccessTokens.claims(SUBJECT, List.of("USER")).audience("nanolink").build();

        mvc.perform(get("/api/me").header(HEADER, TestAccessTokens.sign(claims, TestAccessTokens.signingKey())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenFromAnotherIssuer_isRejected() throws Exception {
        JWTClaimsSet claims = TestAccessTokens.claims(SUBJECT, List.of("USER"))
                .issuer("https://evil.example.test/realms/luppol").build();

        mvc.perform(get("/api/me").header(HEADER, TestAccessTokens.sign(claims, TestAccessTokens.signingKey())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expiredToken_isRejected() throws Exception {
        JWTClaimsSet claims = TestAccessTokens.claims(SUBJECT, List.of("USER"))
                .expirationTime(Date.from(Instant.now().minusSeconds(600))).build();

        mvc.perform(get("/api/me").header(HEADER, TestAccessTokens.sign(claims, TestAccessTokens.signingKey())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void forgedSignature_isRejected() throws Exception {
        mvc.perform(get("/api/me").header(HEADER, TestAccessTokens.signedWithAnotherKey(SUBJECT, List.of("USER"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenWithoutAPlannerRole_isForbidden() throws Exception {
        mvc.perform(get("/api/me").header(HEADER, TestAccessTokens.token(SUBJECT, List.of("offline_access"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void guestToken_isRecognised() throws Exception {
        mvc.perform(get("/api/me").header(HEADER, TestAccessTokens.token("guest-" + SUBJECT, List.of("guest"))))
                .andExpect(jsonPath("$.isGuest").value(true));
    }
}
