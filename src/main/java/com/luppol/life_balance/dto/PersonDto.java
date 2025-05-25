package com.luppol.life_balance.dto;

public record PersonDto (
        Long id,
        String firstName,
        String lastName,
        String middleName,
        String phoneNumber,
        String address,
        Long missionId
) {}
