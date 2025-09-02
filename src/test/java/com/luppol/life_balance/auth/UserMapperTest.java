package com.luppol.life_balance.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luppol.life_balance.auth.dto.UserCreateDto;
import com.luppol.life_balance.auth.dto.UserPutDto;
import com.luppol.life_balance.auth.dto.UserReadDto;
import com.luppol.life_balance.auth.mappers.UserMapper;
import com.luppol.life_balance.auth.moderls.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toReadDto_mapsAllFields() throws Exception {
        User user = User.builder()
                .id(42L)
                .username("A")
                .email("B")
                .password("C")
                .build();
        UserReadDto dto = mapper.toReadDto(user);
        assertThat(dto.id()).isEqualTo(42L);
        assertThat(dto.username()).isEqualTo("A");
        assertThat(dto.email()).isEqualTo("B");
        assertThat(objectMapper.writeValueAsString(dto)).doesNotContain("password");
    }

    @Test
    void toUser_mapsCreateDto() {
        UserCreateDto dto = new UserCreateDto("X", "Y", "Z");
        User user = mapper.toUser(dto);
        assertThat(user.getUsername()).isEqualTo("X");
        assertThat(user.getEmail()).isEqualTo("Y");
        assertThat(user.getPassword()).isEqualTo("Z");
    }

    @Test
    void putFromDtoToUser_overwritesAllFields() {
        UserPutDto putDto = new UserPutDto("M", "N", "Pass@!1");
        User user = User.builder().id(1L).password("O").username("X").email("Y").build();
        mapper.putFromDtoToUser(putDto, user);
        assertThat(user.getUsername()).isEqualTo("M");
        assertThat(user.getEmail()).isEqualTo("N");
        assertThat(user.getPassword()).isEqualTo("Pass@!1");
        assertThat(user.getId()).isEqualTo(1L);
    }

    @Test
    void toReadDto_nullUser_returnsNull() {
        assertThat(mapper.toReadDto(null)).isNull();
    }

    @Test
    void toUser_nullCreateDto_returnsNull() {
        assertThat(mapper.toUser((UserCreateDto) null)).isNull();
    }

    @Test
    void putFromDtoToUser_nullDto_noChanges() {
        User user = User.builder().username("A").build();
        mapper.putFromDtoToUser(null, user);
        assertThat(user.getUsername()).isEqualTo("A");
    }
}
