package com.luppol.life_balance.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPutDto(
        @Size(max = 256) String username,
        @NotBlank @Size(max = 256) String email
) {
    public UserPutDto(String email) {
        this(null, email);
    }
}
