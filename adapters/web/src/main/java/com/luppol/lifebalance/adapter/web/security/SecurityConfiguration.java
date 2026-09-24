package com.luppol.lifebalance.adapter.web.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {
    private static final String CONTENT_SECURITY_POLICY = String.join("; ",
            "default-src 'self'",
            "script-src 'self'",
            "style-src 'self' 'unsafe-inline'",
            "img-src 'self' data:",
            "font-src 'self'",
            "connect-src 'self'",
            "object-src 'none'",
            "base-uri 'none'",
            "form-action 'self'",
            "frame-ancestors 'none'");

    @Bean
    SecurityFilterChain plannerSecurity(HttpSecurity http, JwtAuthenticationConverter realmRolesAuthentication)
            throws Exception {
        return http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/**")
                        .hasAnyRole(PlannerRoles.MEMBER, PlannerRoles.ADMINISTRATOR, PlannerRoles.GUEST)
                        .anyRequest().permitAll())
                .oauth2ResourceServer(server -> server.jwt(jwt -> jwt.jwtAuthenticationConverter(realmRolesAuthentication)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new FetchMetadataFilter(), BearerTokenAuthenticationFilter.class)
                .headers(headers -> headers.contentSecurityPolicy(policy -> policy.policyDirectives(CONTENT_SECURITY_POLICY)))
                .build();
    }

    @Bean
    BearerTokenResolver accessTokenHeader(@Value("${lifebalance.security.access-token-header}") String headerName) {
        return new HeaderBearerTokenResolver(headerName);
    }

    @Bean
    JwtAuthenticationConverter realmRolesAuthentication() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RealmRolesConverter());
        return converter;
    }
}
