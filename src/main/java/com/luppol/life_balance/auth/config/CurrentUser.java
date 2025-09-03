package com.luppol.life_balance.auth.config;

import java.security.Principal;

public record CurrentUser(Long uid) implements Principal {
    @Override public String getName() { return String.valueOf(uid); }
}