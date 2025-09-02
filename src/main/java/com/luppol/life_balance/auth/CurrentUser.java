package com.luppol.life_balance.auth;

import java.security.Principal;

public record CurrentUser(Long uid) implements Principal {
    @Override public String getName() { return String.valueOf(uid); }
}