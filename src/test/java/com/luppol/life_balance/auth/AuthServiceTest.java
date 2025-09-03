package com.luppol.life_balance.auth;

import com.luppol.life_balance.auth.dto.AuthRegisterDto;
import com.luppol.life_balance.auth.mappers.AuthMapper;
import com.luppol.life_balance.auth.services.AuthService;
import com.luppol.life_balance.exceptions.DuplicateUserException;
import com.luppol.life_balance.exceptions.UserEmailValidationException;
import com.luppol.life_balance.exceptions.UserPasswordValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock IUserService userService;
    @Mock
    AuthMapper authMapper;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthService authService;

    @Test
    void register_valid_withExplicitUsername_normalizes_encodes_and_callsCreate() {
        AuthRegisterDto in = new AuthRegisterDto(" John  ", "  John@Example.com ", "StrongP@ss1!");
        // Mapper is a pure pass-through from (already normalized/encoded) inbound dto to UserCreateDto
        when(authMapper.toUserCreateDto(any(AuthRegisterDto.class), "hashedPass"))
                .thenAnswer(inv -> {
                    AuthRegisterDto src = inv.getArgument(0);
                    return new UserCreateDto(src.username(), src.email(), src.password());
                });
        when(passwordEncoder.encode("StrongP@ss1!")).thenReturn("{argon2id}ENC");

        authService.register(in);

        ArgumentCaptor<UserCreateDto> captor = ArgumentCaptor.forClass(UserCreateDto.class);
        verify(userService).create(captor.capture());
        UserCreateDto dto = captor.getValue();

        assertEquals("John", dto.username());                   // trimmed, preserved
        assertEquals("john@example.com", dto.email());          // trimmed + lowercased
        assertEquals("{argon2id}ENC", dto.password());          // hashed

        verify(passwordEncoder).encode("StrongP@ss1!");
        verify(authMapper).toUserCreateDto(any(AuthRegisterDto.class), "hashedPass");
        verifyNoMoreInteractions(userService, authMapper, passwordEncoder);
    }

    @Test
    void register_valid_withoutUsername_derivesUsernameFromEmail_normalizes_and_encodes() {
        AuthRegisterDto in = new AuthRegisterDto(null, "  User@EXAMPLE.org  ", "Stronger1!");
        when(authMapper.toUserCreateDto(any(AuthRegisterDto.class), "hashedPass"))
                .thenAnswer(inv -> {
                    AuthRegisterDto src = inv.getArgument(0);
                    return new UserCreateDto(src.username(), src.email(), src.password());
                });
        when(passwordEncoder.encode("Stronger1!")).thenReturn("{argon2id}HASH");

        authService.register(in);

        ArgumentCaptor<UserCreateDto> captor = ArgumentCaptor.forClass(UserCreateDto.class);
        verify(userService).create(captor.capture());
        UserCreateDto dto = captor.getValue();

        assertEquals("user@example.org", dto.username());       // derived = normalized email
        assertEquals("user@example.org", dto.email());          // normalized email
        assertEquals("{argon2id}HASH", dto.password());         // hashed
    }

    @Test
    void register_invalidEmail_format_throws_and_doesNotCallUserServiceOrMapper() {
        AuthRegisterDto in = new AuthRegisterDto("x", "not-an-email", "StrongP@ss1!");
        assertThrows(UserEmailValidationException.class, () -> authService.register(in));
        verifyNoInteractions(userService, authMapper);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void register_weakPassword_throws_and_doesNotCallUserServiceOrMapper() {
        AuthRegisterDto in = new AuthRegisterDto("x", "a@b.com", "short1!");
        assertThrows(UserPasswordValidationException.class, () -> authService.register(in));
        verifyNoInteractions(userService, authMapper, passwordEncoder);
    }

    @Test
    void register_propagates_duplicate_fromUserService() {
        AuthRegisterDto in = new AuthRegisterDto("u", "u@x.com", "StrongP@ss1!");
        when(authMapper.toUserCreateDto(any(AuthRegisterDto.class), "hashedPass"))
                .thenAnswer(inv -> {
                    AuthRegisterDto src = inv.getArgument(0);
                    return new UserCreateDto(
                            src.username() == null || src.username().isBlank()
                                    ? src.email().trim().toLowerCase(java.util.Locale.ROOT)
                                    : src.username().trim(),
                            src.email().trim().toLowerCase(java.util.Locale.ROOT),
                            "{argon2id}ENC");
                });
        when(passwordEncoder.encode("StrongP@ss1!")).thenReturn("{argon2id}ENC");
        doThrow(new DuplicateUserException("email exists"))
                .when(userService).create(any(UserCreateDto.class));

        assertThrows(DuplicateUserException.class, () -> authService.register(in));
        verify(userService).create(any(UserCreateDto.class));
    }
}
