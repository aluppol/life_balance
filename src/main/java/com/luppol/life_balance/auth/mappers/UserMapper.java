package com.luppol.life_balance.auth.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.luppol.life_balance.auth.dto.UserCreateDto;
import com.luppol.life_balance.auth.dto.UserPatchDto;
import com.luppol.life_balance.auth.dto.UserPutDto;
import com.luppol.life_balance.auth.dto.UserReadDto;
import com.luppol.life_balance.auth.moderls.User;
import com.luppol.life_balance.mappers.BaseMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        builder = @Builder(disableBuilder = true)
)
public interface UserMapper extends BaseMapper {
    UserReadDto toReadDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(UserCreateDto dto);

    @Mapping(target = "id", ignore = true)
    void putFromDtoToUser(UserPutDto dto, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    default void patchFromDtoToUser(UserPatchDto dto, JsonNode jsonBody, @MappingTarget User user) {
        patch(jsonBody, "username", dto.username(), user::setUsername);
        patch(jsonBody, "email", dto.email(), user::setEmail);
        patch(jsonBody, "password", dto.password(), user::setPassword);
    }
}