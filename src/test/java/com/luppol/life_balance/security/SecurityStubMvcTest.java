package com.luppol.life_balance.security;

import com.luppol.life_balance.config.SecurityStubConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest
@Import(SecurityStubConfig.class)
@ActiveProfiles("stub")
public class SecurityStubMvcTest {
    @Autowired
    MockMvc mvc;

    @RestController
    static class TestController {
        @GetMapping("/ping")
        String ping() {
            return "pong";
        }
    }

    @Test
    public void auth_successful() throws Exception {
        mvc.perform(get("ping").header("X-PERSON-ID", "123"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

    @Test
    public void auth_failed() throws Exception {
        mvc.perform((get("ping"))).andExpect(status().isUnauthorized());
    }
}
