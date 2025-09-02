package com.luppol.life_balance.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRegisterDto(
        @Size(max = 256) String username,
        @NotBlank @Size(max = 256) @Email String email,
        @NotBlank @Size(max = 256) String password
) {
    public AuthRegisterDto(String email, String password) {
        this(null, email, password);
    }
}
