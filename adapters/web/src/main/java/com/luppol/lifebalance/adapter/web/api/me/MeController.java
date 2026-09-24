package com.luppol.lifebalance.adapter.web.api.me;

import com.luppol.lifebalance.adapter.web.security.Principals;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@Tag(name = "Me", description = "The signed-in person")
public class MeController {
    @GetMapping
    @Operation(summary = "Who is signed in, and whether it is the shared guest account")
    public MeResponse me(Authentication authentication) {
        return new MeResponse(Principals.displayName(authentication), Principals.isGuest(authentication));
    }
}
