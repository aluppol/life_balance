package com.luppol.life_balance.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MissionPatchDto(
        @Size(max = 2048)
        @Pattern(regexp = "^\\s*\\S.*$", message = "Must not be blank")
        String text
) {}
