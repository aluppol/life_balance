package com.luppol.life_balance.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        @Size(max = 256) String username,
        @NotBlank @Size(max = 256) String email,
        @NotBlank @Size(max = 256) String password
) {
    public UserCreateDto(String email, String password) {
        this(null, email, password);
    }
}
