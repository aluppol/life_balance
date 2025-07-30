package com.luppol.life_balance.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
@Profile("prod")
public class SecurityAuthContext implements AuthContext{
    @Override
    public Long personId() {
        return ((AuthUser) SecurityContextHolder.getContext()
                .getAuthentication(). getPrincipal()).personId();
    }
}
