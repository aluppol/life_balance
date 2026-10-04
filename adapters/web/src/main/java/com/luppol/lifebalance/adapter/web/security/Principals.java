package com.luppol.lifebalance.adapter.web.security;

import com.luppol.lifebalance.domain.person.PersonId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;
import java.util.stream.Stream;

public final class Principals {
    private static final String NAME_CLAIM = "name";
    private static final String USERNAME_CLAIM = "preferred_username";
    private static final String SESSION_CLAIM = "sid";
    private static final String VISIT_SEPARATOR = ":";

    private Principals() {
    }

    public static PersonId personId(Authentication authentication) {
        Jwt jwt = token(authentication);
        return isGuest(authentication) ? guestVisit(jwt) : new PersonId(jwt.getSubject());
    }

    public static boolean isGuest(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(PlannerRoles.GUEST_AUTHORITY::equals);
    }

    public static String displayName(Authentication authentication) {
        Jwt jwt = token(authentication);
        return Stream.of(jwt.getClaimAsString(NAME_CLAIM), jwt.getClaimAsString(USERNAME_CLAIM))
                .flatMap(claim -> Optional.ofNullable(claim).filter(value -> !value.isBlank()).stream())
                .findFirst()
                .orElse(jwt.getSubject());
    }

    private static PersonId guestVisit(Jwt jwt) {
        String session = jwt.getClaimAsString(SESSION_CLAIM);
        if (session == null || session.isBlank()) {
            throw new InvalidBearerTokenException("A guest access token must carry its session id (sid)");
        }
        return new PersonId(jwt.getSubject() + VISIT_SEPARATOR + session);
    }

    private static Jwt token(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            return jwtAuthentication.getToken();
        }
        throw new IllegalStateException("Planner requests are authenticated with a Keycloak access token");
    }
}
