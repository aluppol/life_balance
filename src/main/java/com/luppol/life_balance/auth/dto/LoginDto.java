package com.luppol.life_balance.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDto (
        @NotBlank @Size(max = 256) String username,
        @NotBlank @Size(min = 8, max = 256) String password
){}
