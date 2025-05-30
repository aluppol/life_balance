package com.luppol.life_balance.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

public record PersonPatchDto(
        Optional<@NotBlank String> firstName,
        Optional<@NotBlank String> lastName,
        Optional<String> middleName,
        Optional<String> phoneNumber,
        Optional<String> address
) {}
