package com.luppol.life_balance.auth.dto;

public record UserReadDto(
        Long id,
        String username,
        String email
) {}
