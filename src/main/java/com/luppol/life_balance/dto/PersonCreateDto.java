package com.luppol.life_balance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PersonCreateDto (
        @NotBlank @Size(max = 128) String firstName,
        @NotBlank @Size(max = 128) String lastName,
        @NotBlank @Size(max = 255) String email,
        @Size(max = 255) String middleName,
        @Size(max = 20) String phoneNumber,
        @Size(max = 255) String address
) {}
