package com.luppol.life_balance.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerIT {

    @Autowired
    MockMvc mvc;

    @Container
    static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("db/create-schema.sql");

    @DynamicPropertySource
    static void cfg(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", db::getJdbcUrl);
        r.add("spring.datasource.username", db::getUsername);
        r.add("spring.datasource.password", db::getPassword);
        // If your app requires a JWT secret property, uncomment and adjust:
        // r.add("security.jwt.secret", () -> "test-secret");
        // r.add("application.security.jwt.secret-key", () -> "test-secret");
    }

    // ---- Contract constants (single source of truth) ----
    private static final String BASE           = AuthController.BASE_PATH; // e.g. "/auth"
    private static final String EP_REGISTER    = BASE + "/register";
    private static final String EP_LOGIN       = BASE + "/login";
    private static final String EP_ME          = BASE + "/me";
    private static final String EP_REFRESH     = BASE + "/refresh";
    private static final String EP_LOGOUT      = BASE + "/logout";
    private static final String EP_PASSWORD    = BASE + "/password";
    private static final String REFRESH_COOKIE = BASE + "refreshToken";

    // =========================
    // Use Case: Registration & bootstrap session
    // =========================
    /**
     * Contract:
     * - POST {EP_REGISTER}, JSON body {"username","email","password"}.
     * - 201 Created; body {"accessToken":"<JWT>"}; Set-Cookie: {REFRESH_COOKIE}=<opaque>; HttpOnly; Secure; SameSite=Lax; Path={COOKIE_PATH}.
     * - Email is normalized (trim + lowercase); duplicate email case-insensitive → 409.
     * - Validation errors → 400. Content negotiation enforced (415/406).
     */
    @Nested class Registration {
        /** 201; returns access token; sets refresh cookie; /me with token → 200. */
        @Test void register_201_returnsToken_and_me_200() throws Exception {
            // 1) Register
            String payload = """
        {"username":"john-example","email":"john@example.com","password":"StrongPass1!"}
    """;

            MvcResult res = mvc.perform(post(EP_REGISTER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andReturn();

            // 2) Extract access token
            String body = res.getResponse().getContentAsString();
            String accessToken = new ObjectMapper()
                    .readTree(body)
                    .path("accessToken")
                    .asText();
            org.junit.jupiter.api.Assertions.assertFalse(accessToken.isBlank(), "accessToken should not be blank");

            // 3) Assert refresh cookie with security flags
            List<String> setCookies = res.getResponse().getHeaders("Set-Cookie");
            assertFalse(setCookies.isEmpty(), "Set-Cookie header missing");
            String refresh = setCookies.stream()
                    .filter(c -> c.startsWith(REFRESH_COOKIE + "="))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Refresh cookie not set"));
            assertTrue(refresh.contains("; HttpOnly"), "Refresh cookie must be HttpOnly");
            assertTrue(refresh.contains("; Secure"), "Refresh cookie must be Secure");
            assertTrue(refresh.contains("SameSite=Lax"), "Refresh cookie must have SameSite=Lax");
            assertTrue(refresh.contains("Path=/"), "Refresh cookie must have Path=/");

            // 4) /me with access token → 200
            mvc.perform(get(EP_ME)
                            .header("Authorization", "Bearer " + accessToken)
                            .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("john"))
                    .andExpect(jsonPath("$.email").value("john@example.com"))
                    .andExpect(jsonPath("$.id").isNumber());
        }

        /** Duplicate email (case-insensitive) → 409. Body: {"email":"dup@example.com","password":"StrongPass1!"}. */
        @Test void register_409_duplicateEmail() throws Exception { }

        /** Email normalization: "  Norm@Example.COM  " stored as "norm@example.com" → 201. */
        @Test void register_201_emailNormalizedToLowercase() throws Exception { }

        /** Invalid payload → 400. Body: {"username":"","email":"not-an-email","password":""}. */
        @Test void register_400_invalidPayload() throws Exception { }

        /** Malformed JSON → 400. */
        @Test void register_400_malformedJson() throws Exception { }

        /** Unsupported Content-Type → 415 (Content-Type: text/plain). */
        @Test void register_415_unsupportedContentType() throws Exception { }

        /** Unacceptable Accept → 406 (Accept: application/xml). */
        @Test void register_406_unacceptableAcceptHeader() throws Exception { }

        /** Username too long boundary → 400. */
        @Test void register_400_usernameTooLong() throws Exception { }

        /** Password too short boundary → 400. */
        @Test void register_400_passwordTooShort() throws Exception { }
    }

    // =========================
    // Use Case: Login (issue access token + set refresh cookie)
    // =========================
    /**
     * Contract:
     * - POST {EP_LOGIN} with either {"username","password"} or {"email","password"}.
     * - 200; body {"accessToken":"<JWT>"}; Set-Cookie: {REFRESH_COOKIE}=<opaque>; HttpOnly; Secure; SameSite=Lax; Path={COOKIE_PATH}.
     * - Wrong credentials → 401. Disabled user → 403.
     */
    @Nested class Login {

        /** Login by username → 200; returns access token; sets refresh cookie. */
        @Test void login_200_byUsername_returnsToken_and_setsRefreshCookie() throws Exception { }

        /** Login by email → 200; returns access token; sets refresh cookie. */
        @Test void login_200_byEmail_returnsToken_and_setsRefreshCookie() throws Exception { }

        /** Wrong password (username) → 401. */
        @Test void login_401_wrongPassword_username() throws Exception { }

        /** Wrong password (email) → 401. */
        @Test void login_401_wrongPassword_email() throws Exception { }

        /** Disabled user → 403. Body: {"username":"disabled","password":"StrongPass1!"}. */
        @Test void login_403_userDisabled() throws Exception { }

        /** Rate limit exceeded → 429; includes Retry-After. Repeated wrong credentials within window. */
        @Test void login_429_rateLimited_includesRetryAfter() throws Exception { }

        /** Cookie security flags present on successful login. */
        @Test void cookieAttributes_onLogin_refreshCookieHasSecurityFlags() throws Exception { }
    }

    // =========================
    // Use Case: Access profile (/me)
    // =========================
    /**
     * Contract:
     * - GET {EP_ME} with Authorization: Bearer <JWT>.
     * - 200; body {"id","username","email"} only (no sensitive fields).
     * - Missing/expired/invalid/nbf/aud mismatch → 401. Disabled after issuance → 403.
     */
    @Nested class Me {

        /** Without Authorization → 401 problem+json (type,title,status,detail,instance). */
        @Test void me_401_withoutToken() throws Exception { }

        /** With valid access token → 200 and profile (id, username, email). */
        @Test void me_200_withValidAccess_returnsProfile() throws Exception { }

        /** Expired access token → 401. */
        @Test void me_401_expiredAccessToken() throws Exception { }

        /** Invalid signature → 401. */
        @Test void me_401_invalidSignature() throws Exception { }

        /** Wrong audience (aud != {JWT_AUD}) → 401. */
        @Test void me_401_wrongAudience() throws Exception { }

        /** Not-before (nbf) in future → 401. */
        @Test void me_401_notBeforeInFuture() throws Exception { }

        /** Disabled user after token issued → 403. */
        @Test void me_403_userDisabled() throws Exception { }

        /** Profile does not leak sensitive fields. */
        @Test void me_200_profileDoesNotLeakSensitiveFields() throws Exception { }

        /** Error contract shape on unauthorized (problem+json fields present). */
        @Test void errorContract_problemJson_shape_onUnauthorized() throws Exception { }
    }

    // =========================
    // Use Case: Refresh session (rotate refresh cookie, issue new access)
    // =========================
    /**
     * Contract:
     * - POST {EP_REFRESH} with Cookie: {REFRESH_COOKIE}=<valid>.
     * - 200; body {"accessToken":"<JWT>"}; Set-Cookie: rotated {REFRESH_COOKIE}.
     * - Missing/invalid/expired/reused → 401. Using access token here is invalid → 401.
     */
    @Nested class Refresh {

        /** Valid refresh cookie → 200; rotates cookie; issues new access token. */
        @Test void refresh_200_rotates_andIssuesNewAccess() throws Exception { }

        /** Invalid/expired refresh cookie → 401. */
        @Test void refresh_401_invalidOrExpired() throws Exception { }

        /** Missing refresh cookie → 401. */
        @Test void refresh_401_missingCookie() throws Exception { }

        /** Reuse of rotated refresh cookie → 401; family revoked. */
        @Test void refresh_401_reuseDetected_revokesFamily() throws Exception { }

        /** Access token misused at /refresh (no refresh cookie) → 401. */
        @Test void refresh_401_accessTokenMisused() throws Exception { }

        /** Refresh after logout/revocation → 401. */
        @Test void refresh_401_afterLogout() throws Exception { }
    }

    // =========================
    // Use Case: Logout (revoke family and clear cookie)
    // =========================
    /**
     * Contract:
     * - POST {EP_LOGOUT} with Authorization: Bearer <JWT> and refresh cookie.
     * - 204; Set-Cookie: {REFRESH_COOKIE}=; Max-Age=0; Path={COOKIE_PATH}; HttpOnly; Secure; SameSite=Lax.
     * - All refresh tokens in family invalidated.
     */
    @Nested class Logout {

        /** 204; refresh family revoked; cookie cleared with deletion attributes. */
        @Test void logout_204_revokesFamily_and_clearsCookie() throws Exception { }

        /** Cookie cleared has Max-Age=0 and Path={COOKIE_PATH}. */
        @Test void cookieCleared_onLogout_hasDeletionAttributes() throws Exception { }
    }

    // =========================
    // Use Case: Change password (invalidate tokens, enforce policy)
    // =========================
    /**
     * Contract:
     * - PUT {EP_PASSWORD} with Authorization and JSON {"currentPassword","newPassword"}.
     * - 200 on success; old creds fail; new creds succeed; all prior tokens invalid per policy.
     * - Weak new → 400; wrong current → 403.
     */
    @Nested class ChangePassword {

        /** Success → 200; old login fails; new login succeeds. */
        @Test void changePassword_200_revokesOld_and_allowsNewLogin() throws Exception { }

        /** Weak new password → 400. */
        @Test void changePassword_400_weakNewPassword() throws Exception { }

        /** Wrong current password → 403. */
        @Test void changePassword_403_wrongCurrent() throws Exception { }
    }

    // =========================
    // Use Case: CORS & method safety
    // =========================
    /**
     * Contract:
     * - OPTIONS preflight on auth endpoints returns allow headers for SPA origin and credentials.
     * - Non-allowed HTTP methods → 405.
     */
    @Nested class CorsAndMethodSafety {

        /** Preflight on /login allows origin, methods, headers, and credentials. */
        @Test void cors_preflight_login_allowsOriginAndMethods() throws Exception { }

        /** GET on /login → 405 Method Not Allowed. */
        @Test void methodNotAllowed_405_getOnLogin() throws Exception { }
    }

    // =========================
    // Use Case: JWT key rotation
    // =========================
    /**
     * Contract:
     * - After key rotation (kid changes), tokens signed with old key are rejected; new key tokens accepted.
     */
    @Nested class JwtKeyRotation {

        /** /me with old-signed token → 401; with new-signed token → 200. */
        @Test void jwt_keyRotation_rejectsOld_acceptsNew() throws Exception { }
    }
}