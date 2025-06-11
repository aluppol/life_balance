package com.luppol.life_balance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MissionCreateDto(
        @NotBlank @Size(max = 2048) String text
) {}
