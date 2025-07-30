package com.luppol.life_balance.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("stub")
public class StubAuthContext implements AuthContext {
    @Override
    public Long personId() {
        return 1L;
    }
}
