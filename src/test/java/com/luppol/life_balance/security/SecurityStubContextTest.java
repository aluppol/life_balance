package com.luppol.life_balance.security;

import com.luppol.life_balance.config.SecurityStubConfig;
import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SecurityStubContextTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(SecurityStubConfig.class);

    @Test
    public void filterPopulatesSecurityContext() {
        contextRunner.run(ctx -> {
            RequestHeaderAuthenticationFilter filter = ctx.getBean(RequestHeaderAuthenticationFilter.class);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-PERSON-ID", "123");

            filter.doFilter(request, new MockHttpServletResponse(), (req, res) -> {
                assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
                assertThat(SecurityContextHolder.getContext().getAuthentication()
                        .getPrincipal()).isEqualTo("123");
            });
        });
    }
}

