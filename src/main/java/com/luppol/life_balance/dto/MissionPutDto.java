package com.luppol.life_balance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MissionPutDto(
        @NotBlank @Size(max = 2048) String text
) {}
