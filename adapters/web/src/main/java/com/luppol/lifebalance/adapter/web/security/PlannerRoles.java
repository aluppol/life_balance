package com.luppol.lifebalance.adapter.web.security;

public final class PlannerRoles {
    public static final String MEMBER = "USER";
    public static final String ADMINISTRATOR = "ADMIN";
    public static final String GUEST = "guest";
    public static final String GUEST_AUTHORITY = "ROLE_" + GUEST;

    private PlannerRoles() {
    }
}
