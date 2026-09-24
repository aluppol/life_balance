package com.luppol.lifebalance.adapter.web;

import com.luppol.lifebalance.adapter.web.security.CurrentPersonResolver;
import com.luppol.lifebalance.adapter.web.security.EnrollmentInterceptor;
import com.luppol.lifebalance.application.person.PersonCommands;
import com.luppol.lifebalance.application.person.PersonQueries;
import com.luppol.lifebalance.domain.planning.WeekStart;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.util.List;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebConfiguration implements WebMvcConfigurer {
    private final PersonQueries personQueries;
    private final PersonCommands personCommands;

    public WebConfiguration(PersonQueries personQueries, PersonCommands personCommands) {
        this.personQueries = personQueries;
        this.personCommands = personCommands;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentPersonResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new EnrollmentInterceptor(personQueries, personCommands)).addPathPatterns("/api/**");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, WeekStart.class, text -> new WeekStart(LocalDate.parse(text)));
    }
}
