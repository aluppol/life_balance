package com.luppol.life_balance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Optional;

public record PersonPatchDto(
        @Size(max = 128)
        @Pattern(regexp = "^\\s*\\S.*$", message = "Must not be blank")
        String firstName,
        @Size(max = 128)
        @Pattern(regexp = "^\\s*\\S.*$", message = "Must not be blank")
        String lastName,
        @Size(max = 255)
        @Pattern(regexp = "^\\s*\\S.*$", message = "Must not be blank")
        String email,
        @Size(max = 255) String middleName,
        @Size(max = 20) String phoneNumber,
        @Size(max = 255) String address
) {}
