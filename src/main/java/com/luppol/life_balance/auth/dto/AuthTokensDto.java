package com.luppol.life_balance.auth.dto;

public record AuthTokensDto(
        String accessToken,
        String refreshToken
) {}
