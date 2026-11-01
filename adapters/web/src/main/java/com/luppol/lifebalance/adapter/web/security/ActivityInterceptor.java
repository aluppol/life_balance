package com.luppol.lifebalance.adapter.web.security;

import com.luppol.lifebalance.application.person.PersonCommands;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

public class ActivityInterceptor implements HandlerInterceptor {
    private final PersonCommands personCommands;

    public ActivityInterceptor(PersonCommands personCommands) {
        this.personCommands = personCommands;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        personCommands.recordActivity(Principals.personId(SecurityContextHolder.getContext().getAuthentication()));
        return true;
    }
}
