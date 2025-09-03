package com.luppol.life_balance.auth.mappers;

import com.luppol.life_balance.auth.dto.AuthRegisterDto;
import com.luppol.life_balance.auth.models.User;
import org.mapstruct.*;
import java.util.Locale;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        builder = @Builder(disableBuilder = true)
)
public interface AuthMapper {

    @Mapping(target = "email",    expression = "java(normalize(body.email()))")
    @Mapping(target = "username", expression = "java(normalize(deriveUsername(body.username(), body.email())))")
    @Mapping(target = "password", source = "passwordHash")
    User toUser(AuthRegisterDto body, String passwordHash);

    default String normalize(String username) {
        return username == null ? null : username.trim().toLowerCase(Locale.ROOT);
    }
    default String deriveUsername(String username, String email) {
        return username != null && !username.isBlank() ? username : email;
    }
}
