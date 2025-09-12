package com.luppol.life_balance.auth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public final class AuthUserJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
    @Override public AbstractAuthenticationToken convert(Jwt jwt) {
        AuthUser u = AuthUser.from(jwt);
        Collection<GrantedAuthority> authorities = scopes.convert(jwt);
        return new UsernamePasswordAuthenticationToken(u, jwt, authorities);
    }
}
