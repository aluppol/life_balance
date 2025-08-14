package com.luppol.life_balance.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.luppol.life_balance.dto.UserCreateDto;
import com.luppol.life_balance.dto.UserPatchDto;
import com.luppol.life_balance.dto.UserPutDto;
import com.luppol.life_balance.dto.UserReadDto;
import com.luppol.life_balance.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel="spring")
public interface UserMapper extends BaseMapper {
    UserReadDto toReadDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(UserCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void putFromDtoToUser(UserPutDto dto, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    default void patchFromDtoToUser(UserPatchDto dto, JsonNode jsonBody,  @MappingTarget User user) {
        patch(jsonBody, "username", dto.username(), user::setUsername);
        patch(jsonBody, "email", dto.email(), user::setEmail);
    }
}
