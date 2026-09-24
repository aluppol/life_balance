package com.luppol.lifebalance.adapter.web.security;

import com.luppol.lifebalance.domain.person.PersonId;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentPersonResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return PersonId.class.equals(parameter.getParameterType());
    }

    @Override
    public PersonId resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest request,
                                    WebDataBinderFactory binderFactory) {
        return Principals.personId(SecurityContextHolder.getContext().getAuthentication());
    }
}
