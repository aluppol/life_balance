package com.luppol.lifebalance.adapter.web.security;

import com.luppol.lifebalance.application.person.PersonCommands;
import com.luppol.lifebalance.application.person.PersonQueries;
import com.luppol.lifebalance.domain.person.PersonAlreadyEnrolledException;
import com.luppol.lifebalance.domain.person.PersonId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.function.Consumer;

public class EnrollmentInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(EnrollmentInterceptor.class);

    private final PersonQueries personQueries;
    private final PersonCommands personCommands;

    public EnrollmentInterceptor(PersonQueries personQueries, PersonCommands personCommands) {
        this.personQueries = personQueries;
        this.personCommands = personCommands;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonId person = Principals.personId(authentication);
        if (!personQueries.isEnrolled(person)) {
            enrollOnce(person, enrollmentFor(authentication));
        }
        return true;
    }

    private Consumer<PersonId> enrollmentFor(Authentication authentication) {
        return Principals.isGuest(authentication) ? personCommands::enrollGuest : personCommands::enrollMember;
    }

    private static void enrollOnce(PersonId person, Consumer<PersonId> enrollment) {
        try {
            enrollment.accept(person);
        } catch (PersonAlreadyEnrolledException enrolledConcurrently) {
            LOG.debug("{} was enrolled by a concurrent request", person);
        }
    }
}
