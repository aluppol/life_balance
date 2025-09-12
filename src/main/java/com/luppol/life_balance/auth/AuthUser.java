package com.luppol.life_balance.auth;

import org.springframework.security.oauth2.jwt.Jwt;

public record AuthUser(String id, String username, String email) {
    static AuthUser from(Jwt jwt) {
        return new AuthUser(
                jwt.getSubject(),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("email")
        );
    }
}
