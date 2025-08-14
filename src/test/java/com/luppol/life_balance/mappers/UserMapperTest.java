package com.luppol.life_balance.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.luppol.life_balance.dto.UserCreateDto;
import com.luppol.life_balance.dto.UserPatchDto;
import com.luppol.life_balance.dto.UserPutDto;
import com.luppol.life_balance.dto.UserReadDto;
import com.luppol.life_balance.models.Mission;
import com.luppol.life_balance.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    UserMapper mapper = Mappers.getMapper(UserMapper.class);

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
        UserPutDto putDto = new UserPutDto("M", "N");
        User user = User.builder().id(1L).password("O").username("X").email("Y").build();
        mapper.putFromDtoToUser(putDto, user);
        assertThat(user.getUsername()).isEqualTo("M");
        assertThat(user.getEmail()).isEqualTo("N");
        assertThat(user.getPassword()).isEqualTo("O");
        assertThat(user.getId()).isEqualTo(1L);

    }

    @Test
    void patchFromDtoToUser_onlyPatchedFieldsAreChanged() {
        UserPatchDto patchDto = new UserPatchDto("Patched", null);
        ObjectNode json = objectMapper.createObjectNode();
        json.put("username", "Patched");
        User user = User.builder().id(1L).username("Orig").email("Last@sdf.co").password("Passw0rd!").build();
        mapper.patchFromDtoToUser(patchDto, json, user);
        assertThat(user.getUsername()).isEqualTo("Patched");
        assertThat(user.getEmail()).isEqualTo("Last@sdf.co");
        assertThat(user.getPassword()).isEqualTo("Passw0rd!");
        assertThat(user.getId()).isEqualTo(1L);
    }

    @Test
    void patchFromDtoToUser_doesNothingIfFieldNotPresentInJson() {
        UserPatchDto dto = new UserPatchDto("ShouldNotApply", null);
        ObjectNode json = objectMapper.createObjectNode();

        User user = new User();
        user.setUsername("Original");

        mapper.patchFromDtoToUser(dto, json, user);

        assertEquals("Original", user.getUsername());
    }

    @Test
    void patchFromDtoToUser_updatesAllFields() {
        UserPatchDto dto = new UserPatchDto("A", "B");
        ObjectNode json = objectMapper.createObjectNode();
        json.put("username", "A");
        json.put("email", "B");

        User user = new User();
        mapper.patchFromDtoToUser(dto, json, user);

        assertEquals("A", user.getUsername());
        assertEquals("B", user.getEmail());
    }

    @Test
    void patchFromDtoToUser_clearsAllFieldsWithExplicitNulls() {
        UserPatchDto dto = new UserPatchDto(null, null);
        ObjectNode json = objectMapper.createObjectNode();
        json.putNull("username");
        json.putNull("email");

        User user = new User();
        user.setUsername("X");
        user.setEmail("Y");

        mapper.patchFromDtoToUser(dto, json, user);

        assertNull(user.getEmail());
        assertNull(user.getUsername());
    }

    @Test
    void patchFromDtoToUser_doesNothingWhenNoFieldsPresent() {
        UserPatchDto dto = new UserPatchDto("newFirst", "newLast");
        ObjectNode json = objectMapper.createObjectNode();

        User user = new User();
        user.setUsername("OldFirst");

        mapper.patchFromDtoToUser(dto, json, user);

        assertEquals("OldFirst", user.getUsername()); // unchanged
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
