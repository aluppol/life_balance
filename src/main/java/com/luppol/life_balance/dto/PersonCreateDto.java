package com.luppol.life_balance.dto;

import jakarta.validation.constraints.NotBlank;

public record PersonCreateDto (
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        String middleName,
        String phoneNumber,
        String address
) {}
