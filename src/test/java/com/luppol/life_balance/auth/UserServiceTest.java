package com.luppol.life_balance.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luppol.life_balance.auth.mappers.UserMapper;
import com.luppol.life_balance.auth.models.User;
import com.luppol.life_balance.auth.services.UserRepository;
import com.luppol.life_balance.exceptions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    UserRepository userRepository;

    @Mock UserMapper userMapper;

    @InjectMocks UserService userService;


    // --------------------------------- CREATE ---------------------------------

    @Test
    void create_persistsEntity_and_returnsReadDto() {
        UserCreateDto dto = new UserCreateDto("test_user", "test@test.com", "{argon2id}ENC"); // already hashed by AuthService
        User toSave = User.builder().username("test_user").email("test@test.com").password("{argon2id}ENC").build();
        User saved  = User.builder().id(1L).username("test_user").email("test@test.com").password("{argon2id}ENC").build();
        UserReadDto readDto = new UserReadDto(1L, "test_user", "test@test.com");

        when(userMapper.toUser(dto)).thenReturn(toSave);
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("test_user")).thenReturn(false);
        when(userRepository.save(toSave)).thenReturn(saved);
        when(userMapper.toReadDto(saved)).thenReturn(readDto);

        UserReadDto result = userService.create(dto);

        assertEquals(readDto, result);
        verify(userRepository).existsByEmail("test@test.com");
        verify(userRepository).existsByUsername("test_user");
        verify(userRepository).save(toSave);
    }

    @Test
    void create_duplicateEmail_throws_and_doesNotSave() {
        UserCreateDto dto = new UserCreateDto("u", "e@x.com", "{argon2id}ENC");
        when(userRepository.existsByEmail("e@x.com")).thenReturn(true);

        assertThrows(DuplicateUserException.class, () -> userService.create(dto));
        verify(userRepository).existsByEmail("e@x.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_duplicateUsername_throws_and_doesNotSave() {
        UserCreateDto dto = new UserCreateDto("u", "e@x.com", "{argon2id}ENC");
        when(userRepository.existsByEmail("e@x.com")).thenReturn(false);
        when(userRepository.existsByUsername("u")).thenReturn(true);

        assertThrows(DuplicateUserException.class, () -> userService.create(dto));
        verify(userRepository).existsByUsername("u");
        verify(userRepository, never()).save(any());
    }

    // --------------------------------- READ ---------------------------------

    @Test
    void getById_notFound_throws() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getById(42L));
    }

    @Test
    void getById_succeeds() {
        User saved = User.builder().id(7L).username("u").email("e@x.com").build();
        UserReadDto read  = new UserReadDto(7L, "u", "e@x.com");
        when(userRepository.findById(7L)).thenReturn(Optional.of(saved));
        when(userMapper.toReadDto(saved)).thenReturn(read);
        assertEquals(read, userService.getById(7L));
    }

    // --------------------------------- PUT ----------------------------------

    @Test
    void put_notFound_throws() {
        when(userRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.put(9L, new UserPutDto("u","e@x.com", "Pass@!1")));
    }

    @Test
    void put_updates_and_returnsReadDto() {
        long id = 9L;
        User existing = User.builder().id(id).username("old").email("old@x.com").build();
        UserPutDto putDto = new UserPutDto("newu", "new@x.com", "Pass@!1");
        User updated = User.builder().id(id).username(putDto.username()).email(putDto.email())
                .password(putDto.password()).build();
        UserReadDto read = new UserReadDto(id, putDto.username(), putDto.email());

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail(putDto.email())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(putDto.username())).thenReturn(Optional.empty());
        when(userRepository.save(existing)).thenReturn(updated);
        when(userMapper.toReadDto(updated)).thenReturn(read);

        assertEquals(read, userService.put(id, putDto));

        verify(userMapper).putFromDtoToUser(putDto, existing);
        verify(userRepository).save(existing);
    }

    @Test
    void put_duplicateEmail_throws() {
        final long id = 1L;
        User existing = User.builder().id(id).username("u").email("old@x.com").build();
        UserPutDto dto = new UserPutDto("u", "dup@x.com", "Pass@!1");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("dup@x.com")).thenReturn(Optional.of(User.builder().id(2L).build()));

        assertThrows(UserEmailValidationException.class, () -> userService.put(id, dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void put_duplicateUsername_throws() {
        final long id = 1L;
        User existing = User.builder().id(id).username("u").email("old@x.com").build();
        UserPutDto dto = new UserPutDto("newu", "old@x.com", "Pass@!1");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("newu")).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(userRepository.findByEmail("old@x.com")).thenReturn(Optional.of(existing));

        assertThrows(UserUsernamelValidationException.class, () -> userService.put(id, dto));
        verify(userRepository, never()).save(any());
    }

    // -------------------------------- PATCH ---------------------------------

    @Test
    void patch_notFound_throws() {
        when(userRepository.findById(5L)).thenReturn(Optional.empty());
        JsonNode json = objectMapper.createObjectNode().put("email","x@x.com");
        UserPatchDto dto = new UserPatchDto(null, "x@x.com", null);
        assertThrows(NotFoundException.class, () -> userService.patch(5L, dto, json));
    }

    @Test
    void patch_updates_selected_fields_and_returnsReadDto() {
        long id = 3L;

        JsonNode json = objectMapper.createObjectNode().put("email","new@x.com");
        UserPatchDto patchDto = new UserPatchDto(null, "new@x.com", null);

        User existing = User.builder().id(id).username("old").email("old@x.com").build();
        User patched  = User.builder().id(id).username("old").email("new@x.com").build();
        UserReadDto read = new UserReadDto(id, "old", "new@x.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@x.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("old")).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(patched);
        when(userMapper.toReadDto(patched)).thenReturn(read);

        assertEquals(read, userService.patch(id, patchDto, json));

        verify(userMapper).patchFromDtoToUser(patchDto, json, existing);
        verify(userRepository).save(existing);
        verify(userMapper).toReadDto(patched);
    }

    @Test
    void patch_duplicateEmail_throws() {
        long id = 2L;
        User existing = User.builder().id(id).username("u").email("old@x.com").build();
        JsonNode json = objectMapper.createObjectNode().put("email","dup@x.com");
        UserPatchDto patchDto = new UserPatchDto(null, "dup@x.com", null);

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("dup@x.com")).thenReturn(Optional.of(User.builder().id(4L).build()));

        assertThrows(UserEmailValidationException.class, () -> userService.patch(id, patchDto, json));
        verify(userRepository, never()).save(any());
    }

    @Test
    void patch_duplicateUsername_throws() {
        long id = 2L;
        User existing = User.builder().id(id).username("u").email("old@x.com").build();
        JsonNode json = objectMapper.createObjectNode().put("username","dup");
        UserPatchDto patchDto = new UserPatchDto("dup", null, null);

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("dup")).thenReturn(Optional.of(User.builder().id(3L).build()));

        assertThrows(UserUsernamelValidationException.class, () -> userService.patch(id, patchDto, json));
        verify(userRepository, never()).save(any());
    }

    // ------------------------------- DELETE/LIST -----------------------------

    @Test
    void deleteById_callsRepo() {
        long id = 11L;
        userService.deleteById(id);
        verify(userRepository).deleteById(id);
    }
}
