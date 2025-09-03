package com.luppol.life_balance.auth;

import com.luppol.life_balance.auth.dto.AuthRegisterDto;
import com.luppol.life_balance.auth.mappers.AuthMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class AuthMapperTest {
    private final AuthMapper mapper = Mappers.getMapper(AuthMapper.class);

    @Test
    void toUserCreateDto_normalizesEmail_andDerivesUsername_whenMissing() {
        AuthRegisterDto body = new AuthRegisterDto(null, "  John+Tag@Example.COM  ", "StrongP@ss1!");

        UserCreateDto dto = mapper.toUserCreateDto(body, "hash");

        assertThat(dto).isNotNull();
        assertThat(dto.email()).isEqualTo("john+tag@example.com");
        assertThat(dto.username()).isEqualTo("john+tag@example.com");
        assertThat(dto.password()).isEqualTo("hash");
    }

    @Test
    void toUserCreateDto_preservesProvidedUsername_andTrimsInputs() {
        AuthRegisterDto body = new AuthRegisterDto("  John  ", " USER@EXAMPLE.COM ", "StrongP@ss1!");

        UserCreateDto dto = mapper.toUserCreateDto(body, "hash");

        assertThat(dto.username()).isEqualTo("john");
        assertThat(dto.email()).isEqualTo("user@example.com");
        assertThat(dto.password()).isEqualTo("hash");
    }

    @Test
    void toUserCreateDto_blankUsername_usesEmailLowercased() {
        AuthRegisterDto body = new AuthRegisterDto("   ", "Case@Domain.TLD", "StrongP@ss1!");

        UserCreateDto dto = mapper.toUserCreateDto(body, "hash");

        assertThat(dto.username()).isEqualTo("case@domain.tld");
        assertThat(dto.email()).isEqualTo("case@domain.tld");
    }
}
