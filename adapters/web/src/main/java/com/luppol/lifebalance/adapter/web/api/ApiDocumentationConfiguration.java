package com.luppol.lifebalance.adapter.web.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiDocumentationConfiguration {
    @Bean
    OpenAPI plannerApi() {
        return new OpenAPI().info(new Info()
                .title("Life Balance API")
                .description("A weekly planner built on The 7 Habits of Highly Effective People. "
                        + "Every call acts for the person in the Keycloak access token that the gateway passes in "
                        + "X-Forwarded-Access-Token.")
                .version("1")
                .license(new License().name("All rights reserved")));
    }
}
