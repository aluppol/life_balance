package com.luppol.life_balance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import java.util.List;

@Configuration
@Profile("stub")
public class SecurityStubConfig {

    @Bean
    SecurityFilterChain stubChain(HttpSecurity http) throws Exception {
        RequestHeaderAuthenticationFilter headerFilter =new RequestHeaderAuthenticationFilter();

        headerFilter.setPrincipalRequestHeader("X-PERSON-ID");
        headerFilter.setExceptionIfHeaderMissing(false);

        headerFilter.setAuthenticationManager(auth -> new UsernamePasswordAuthenticationToken(
                auth.getPrincipal(), null,
                List.of(new SimpleGrantedAuthority("USER"))
        ));

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterAt(headerFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .build();
    }
}
