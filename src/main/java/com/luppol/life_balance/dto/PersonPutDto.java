package com.luppol.life_balance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PersonPutDto(
        @NotBlank @Size(max = 128) String firstName,
        @NotBlank @Size(max = 128) String lastName,
        @Size(max = 256) String middleName,
        @Size(max = 20) String phoneNumber,
        @Size(max = 256) String address
) {}
