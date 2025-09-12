package com.luppol.life_balance.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {
    @GetMapping("/me")
    Map<String, Object> me (@AuthenticationPrincipal AuthUser u) {
        return Map.of("sub",u.id(),"username",u.username(),"email",u.email());
    }
}
