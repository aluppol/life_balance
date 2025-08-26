package com.luppol.life_balance.auth.config;


import com.luppol.life_balance.auth.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    SecurityFilterChain api(HttpSecurity http, Converter<Jwt, ? extends AbstractAuthenticationToken> conv) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/api/persons/**", "/api/missions/**").permitAll()
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/password-reset/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(conv)));

        return http.build();
    }

    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> currentUserConverter () {
        JwtGrantedAuthoritiesConverter gac = new JwtGrantedAuthoritiesConverter();

        return jwt -> {
            Long uid = jwt.getClaim("uid");
            String uname = jwt.getClaim("uname");
            Long pid = jwt.getClaim("pid");
            CurrentUser principal = new CurrentUser(uid, uname, pid);
            return new UsernamePasswordAuthenticationToken(principal, null, gac.convert(jwt));
        };
    }
}