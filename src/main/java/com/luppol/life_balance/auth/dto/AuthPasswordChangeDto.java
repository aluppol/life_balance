package com.luppol.life_balance.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthPasswordChangeDto(
        @NotBlank @Size(min = 8, max = 256) String oldPassword,
        @NotBlank @Size(min = 8, max = 256) String newPassword
) { }
