package com.luppol.life_balance.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    private final String expeced;

    AudienceValidator(@Value("${app.security.expected-audience}") String expected) {this.expeced = expected; }

    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        List<String> aud = jwt.getAudience();
        return aud != null && aud.contains(expeced)
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "misssing/invalid aud", ""));
    }
}
