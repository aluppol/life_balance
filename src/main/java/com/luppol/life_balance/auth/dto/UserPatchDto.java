package com.luppol.life_balance.auth.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserPatchDto(
        @Size(max = 256)
        @Pattern(regexp = "^\\s*\\S.*$", message = "Must not be blank")
        String username,
        @Size(max = 256)
        @Pattern(regexp = "^\\s*\\S.*$", message = "Must not be blank")
        String email
) {}
