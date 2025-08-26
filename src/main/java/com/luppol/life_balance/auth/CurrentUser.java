package com.luppol.life_balance.auth;

import java.security.Principal;

public record CurrentUser(Long uid, String username, Long pid) implements Principal {
    @Override public String getName() { return String.valueOf(uid); }
}