package com.luppol.life_balance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserPasswordPutDto(
        @NotBlank
        @Size(max = 256)
        @Pattern(regexp = "^\\s*\\S.*$", message = "Password missing")
        String password
) {}
