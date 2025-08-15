package com.luppol.life_balance.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luppol.life_balance.dto.UserCreateDto;
import com.luppol.life_balance.dto.UserPatchDto;
import com.luppol.life_balance.dto.UserPutDto;
import com.luppol.life_balance.dto.UserReadDto;
import com.luppol.life_balance.exceptions.*;
import com.luppol.life_balance.mappers.UserMapper;
import com.luppol.life_balance.models.User;
import com.luppol.life_balance.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void create_succeeds() {
        UserCreateDto dto = new UserCreateDto("test_user", "test@test.com", "passwordA!45");
        User toSave = User.builder().username("test_user").email("test@test.com").password("passwordA!45").build();
        User saved  = User.builder().id(1L).username("test_user").email("test@test.com").build();
        UserReadDto readDto   = new UserReadDto(1L, "test_user", "test@test.com");

        when(userMapper.toUser(dto)).thenReturn(toSave);
        when(userMapper.toReadDto(saved)).thenReturn(readDto);
        when(userRepository.save(toSave)).thenReturn(saved);

        UserReadDto result = userService.create(dto);

        assertEquals(readDto, result);
        verify(userMapper).toUser(dto);
        verify(userMapper).toReadDto(saved);
        verify(userRepository).save(toSave);
    }

    @Test
    void create_succeeds_noUsername() {
        UserCreateDto dto = new UserCreateDto("test@test.com", "passwordA!45");
        User toSave = User.builder().email("test@test.com").password("passwordA!45").build();
        User saved  = User.builder().id(1L).username("test@test.com").email("test@test.com").build();
        UserReadDto readDto   = new UserReadDto(1L, "test@test.com", "test@test.com");

        when(userMapper.toUser(dto)).thenReturn(toSave);
        when(userMapper.toReadDto(saved)).thenReturn(readDto);
        when(userRepository.save(toSave)).thenReturn(saved);

        UserReadDto result = userService.create(dto);

        assertEquals(readDto, result);
        verify(userMapper).toUser(dto);
        verify(userMapper).toReadDto(saved);
        verify(userRepository).save(toSave);
    }

    @Test
    void create_throws_emailAlreadyExists() {
        UserCreateDto dto = new UserCreateDto("u", "e@x.com", "Password!45");
        when(userMapper.toUser(dto)).thenReturn(User.builder().username("u").email("e@x.com").password("Password!45").build());
        when(userRepository.existsByEmail("e@x.com")).thenReturn(true);
        assertThrows(DuplicateUserException.class, () -> userService.create(dto));
        verify(userRepository).existsByEmail("e@x.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_throws_emailFailingPattern() {
        UserCreateDto dto = new UserCreateDto("u", "asdfsda", "Password!45");
        when(userMapper.toUser(dto)).thenReturn(User.builder().email("asdfsda").password("Password!45").build());        assertThrows(UserEmailValidationException.class, () -> userService.create(dto));
        verify(userRepository, never()).existsByEmail("asdfsda");
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_throws_usernameAlreadyExists() {
        UserCreateDto dto = new UserCreateDto("u", "e@x.com", "Password!45");
        when(userMapper.toUser(dto)).thenReturn(User.builder().username("u").email("e@x.com").password("Password!45").build());
        when(userRepository.existsByUsername("u")).thenReturn(true);
        assertThrows(DuplicateUserException.class, () -> userService.create(dto));
        verify(userRepository).existsByUsername("u");
        verify(userRepository, never()).save(any());
    }

    // -------- CREATE (password policy) -----------------------------

    @Test void create_rejects_passwordTooShort() {
        UserCreateDto dto = new UserCreateDto("u", "e@x.com", "A1!a"); // < 8
        User user = User.builder().username("u").email("e@x.com").password("A1!a").build();
        when(userMapper.toUser(dto)).thenReturn(user);

        assertThrows(UserPasswordValidationException.class, () -> userService.create(dto));
    }

    @Test void create_rejects_passwordTooLong() {
        UserCreateDto dto = new UserCreateDto("u", "e@x.com", "asdfsadfasfdasfdsafdsafsdafasdfdsafsdafsdafdsafdsafsdafdsagasfgasdfgdsgasdgsdagdsagadsgasgasdgasdgasdgsdagasgA1!a"); // < 8
        User user = User.builder().username("u").email("e@x.com").password("asdfsadfasfdasfdsafdsafsdafasdfdsafsdafsdafdsafdsafsdafdsagasfgasdfgdsgasdgsdagdsagadsgasgasdgasdgasdgsdagasgA1!a").build();
        when(userMapper.toUser(dto)).thenReturn(user);
        assertThrows(UserPasswordValidationException.class, () -> userService.create(dto));
    }

    @Test void create_rejects_passwordNoDigit() {
        UserCreateDto dto = new UserCreateDto("u", "e@x.com", "Abcdef!G");
        User user = User.builder().username("u").email("e@x.com").password("Abcdef!G").build();
        when(userMapper.toUser(dto)).thenReturn(user);

        assertThrows(UserPasswordValidationException.class, () -> userService.create(dto));
    }

    @Test void create_rejects_passwordNoSpecial() {
        UserCreateDto dto = new UserCreateDto("u", "e@x.com", "Abcdef1G");
        User user = User.builder().username("u").email("e@x.com").password("Abcdef1G").build();
        when(userMapper.toUser(dto)).thenReturn(user);

        assertThrows(UserPasswordValidationException.class, () -> userService.create(dto));
    }

    @Test void create_rejects_passwordNoLowercase() {
        UserCreateDto dto = new UserCreateDto("u", "e@x.com", "PASSWORD1!");
        User user = User.builder().username("u").email("e@x.com").password("PASSWORD1!").build();
        when(userMapper.toUser(dto)).thenReturn(user);

        assertThrows(UserPasswordValidationException.class, () -> userService.create(dto));
    }

    @Test void create_rejects_passwordNoUppercase() {
        UserCreateDto dto = new UserCreateDto("u", "e@x.com", "password1!");
        User user = User.builder().username("u").email("e@x.com").password("password1!").build();
        when(userMapper.toUser(dto)).thenReturn(user);

        assertThrows(UserPasswordValidationException.class, () -> userService.create(dto));
    }

    @Test void create_rejects_passwordContainsQwerty() {
        UserCreateDto dto = new UserCreateDto("u", "e@x.com", "AbcQwErTy1!");
        User user = User.builder().username("u").email("e@x.com").password("AbcQwErTy1!").build();
        when(userMapper.toUser(dto)).thenReturn(user);

        assertThrows(UserPasswordValidationException.class, () -> userService.create(dto));
    }

    @Test
    void create_encodesPassword_andPersistsHash() {
        UserCreateDto dto = new UserCreateDto("u", "e@x.com", "Strong1!");
        User toSave = User.builder().username("u").email("e@x.com").password("Strong1!").build();
        User saved  = User.builder().id(1L).username("u").email("e@x.com").password("{argon2id}ENC").build();
        UserReadDto readDto = new UserReadDto(1L, "u", "e@x.com");

        when(userMapper.toUser(dto)).thenReturn(toSave);
        when(passwordEncoder.encode("Strong1!")).thenReturn("{argon2id}ENC");
        when(userRepository.existsByEmail("e@x.com")).thenReturn(false);
        when(userRepository.existsByUsername("u")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toReadDto(saved)).thenReturn(readDto);

        UserReadDto result = userService.create(dto);
        assertEquals(readDto, result);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("{argon2id}ENC", captor.getValue().getPassword()); // stored hash is encoded

        verify(passwordEncoder).encode("Strong1!");
    }

    @Test
    void create_encodesPassword_whenUsernameMissing_usesEmailAsUsername() {
        UserCreateDto dto = new UserCreateDto("e@x.com", "Strong1!");
        User toSave = User.builder().email("e@x.com").password("Strong1!").build();
        User saved  = User.builder().id(2L).username("e@x.com").email("e@x.com").password("{argon2id}ENC").build();
        UserReadDto readDto = new UserReadDto(2L, "e@x.com", "e@x.com");

        when(userMapper.toUser(dto)).thenReturn(toSave);
        when(passwordEncoder.encode("Strong1!")).thenReturn("{argon2id}ENC");
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toReadDto(saved)).thenReturn(readDto);

        UserReadDto result = userService.create(dto);
        assertEquals(readDto, result);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedArg = captor.getValue();
        assertEquals("{argon2id}ENC", savedArg.getPassword());
        assertEquals("e@x.com", savedArg.getUsername()); // username derived from email

        verify(passwordEncoder).encode("Strong1!");
    }

    @Test
    void create_invalidPassword_doesNotEncodeOrSave() {
        UserCreateDto dto = new UserCreateDto("u", "e@x.com", "short1!");
        User toSave = User.builder().username("u").email("e@x.com").password("short1!").build();
        when(userMapper.toUser(dto)).thenReturn(toSave);

        assertThrows(UserPasswordValidationException.class, () -> userService.create(dto));

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }


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

    @Test
    void put_notFound() {
        when(userRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.put(9L, new UserPutDto("u","e@x.com")));
    }

    @Test
    void put_succeeds() {
        long id = 9L;
        User existing = User.builder().id(id).username("old").email("old@x.com").build();
        UserPutDto putDto = new UserPutDto("newu", "new@x.com");
        User updated = User.builder().id(id).username("newu").email("new@x.com").build();
        UserReadDto read = new UserReadDto(id, "newu", "new@x.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenReturn(updated);
        when(userMapper.toReadDto(updated)).thenReturn(read);

        assertEquals(read, userService.put(id, putDto));

        verify(userMapper).putFromDtoToUser(putDto, existing);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toReadDto(updated);
    }

    @Test
    void put_duplicateEmail_throws() {
        final long ID = 1L;
        User existing = User.builder().id(ID).username("u").email("old@x.com").build();
        UserPutDto dto = new UserPutDto("u", "dup@x.com");

        when(userRepository.findById(ID)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("dup@x.com")).thenReturn(Optional.of(User.builder().id(2L).build()));

        doAnswer(inv -> {
            UserPutDto dtoMpr = inv.getArgument(0);
            User target = inv.getArgument(1);
            target.setUsername(dtoMpr.username());
            target.setEmail(dtoMpr.email());
            return null;
        }).when(userMapper).putFromDtoToUser(dto, existing);

        assertThrows(UserEmailValidationException.class, () -> userService.put(ID, dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void put_duplicateUsername_throws() {
        final long ID = 1L;
        User existing = User.builder().id(ID).username("u").email("old@x.com").build();
        UserPutDto dto = new UserPutDto("newu", "old@x.com");

        when(userRepository.findById(ID)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("newu")).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(userRepository.findByEmail("old@x.com")).thenReturn(Optional.of(existing));

        doAnswer(inv -> {
            UserPutDto dtoMpr = inv.getArgument(0);
            User target = inv.getArgument(1);
            target.setUsername(dtoMpr.username());
            target.setEmail(dtoMpr.email());
            return null;
        }).when(userMapper).putFromDtoToUser(dto, existing);

        assertThrows(UserUsernamelValidationException.class, () -> userService.put(ID, dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void patch_notFound() {
        when(userRepository.findById(5L)).thenReturn(Optional.empty());
        JsonNode json = objectMapper.createObjectNode().put("email","x@x.com");
        UserPatchDto dto = new UserPatchDto(null, "x@x.com");
        assertThrows(NotFoundException.class, () -> userService.patch(5L, dto, json));
    }

    @Test
    void patch_succeeds() {
        long id = 3L;

        JsonNode json = objectMapper.createObjectNode().put("email","new@x.com");
        UserPatchDto patchDto = new UserPatchDto(null, "new@x.com");

        User existing = User.builder().id(id).username("old").email("old@x.com").build();
        User patched  = User.builder().id(id).username("old").email("new@x.com").build();
        UserReadDto read = new UserReadDto(id, "old", "new@x.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@x.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("old")).thenReturn(Optional.of(existing));


        doAnswer(inv -> {
            UserPatchDto dto = inv.getArgument(0);
            JsonNode j = inv.getArgument(1);
            User target = inv.getArgument(2);
            if (dto.username() != null) target.setUsername(dto.username());
            if (dto.email() != null)    target.setEmail(dto.email());
            return null;
        }).when(userMapper).patchFromDtoToUser(patchDto, json, existing);

        when(userRepository.save(existing)).thenReturn(patched);
        when(userMapper.toReadDto(patched)).thenReturn(read);

        assertEquals(read, userService.patch(id, patchDto, json));

        verify(userMapper).patchFromDtoToUser(patchDto, json, existing);
        verify(userRepository).save(existing);
        verify(userMapper).toReadDto(patched);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).existsByUsername(anyString());
    }

    @Test
    void patch_duplicateEmail_throws() {
        long id = 2L;
        User existing = User.builder().id(id).username("u").email("old@x.com").build();
        JsonNode json = objectMapper.createObjectNode().put("email","dup@x.com");
        UserPatchDto patchDto = new UserPatchDto(null, "dup@x.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("dup@x.com")).thenReturn(Optional.of(User.builder().id(4L).build()));

        doAnswer(inv -> {
            UserPatchDto dto = inv.getArgument(0);
            JsonNode j = inv.getArgument(1);
            User target = inv.getArgument(2);
            if (dto.username() != null) target.setUsername(dto.username());
            if (dto.email() != null)    target.setEmail(dto.email());
            return null;
        }).when(userMapper).patchFromDtoToUser(patchDto, json, existing);

        assertThrows(UserEmailValidationException.class, () -> userService.patch(id, patchDto, json));
        verify(userRepository, never()).save(any());
    }

    @Test
    void patch_duplicateUsername_throws() {
        long id = 2L;
        User existing = User.builder().id(id).username("u").email("old@x.com").build();
        JsonNode json = objectMapper.createObjectNode().put("username","dup");
        UserPatchDto patchDto = new UserPatchDto("dup", null);

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("dup")).thenReturn(Optional.of(User.builder().id(3L).build()));

        doAnswer(inv -> {
            UserPatchDto dto = inv.getArgument(0);
            JsonNode j = inv.getArgument(1);
            User target = inv.getArgument(2);
            if (dto.username() != null) target.setUsername(dto.username());
            if (dto.email() != null)    target.setEmail(dto.email());
            return null;
        }).when(userMapper).patchFromDtoToUser(patchDto, json, existing);

        assertThrows(UserUsernamelValidationException.class, () -> userService.patch(id, patchDto, json));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteById_callsRepo() {
        long id = 11L;
        userService.deleteById(id);
        verify(userRepository).deleteById(id);
    }


    @Test
    void changePassword_succeeds_encodesAndPersists() {
        final long ID = 7L;
        User existing = User.builder().id(ID).email("e@x.com").password("ENC_OLD").build();

        when(userRepository.findById(ID)).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("OldP@ss1!", "ENC_OLD")).thenReturn(true);
        when(passwordEncoder.encode("NewP@ssw0rd")).thenReturn("{argon2id}ENC_NEW");
        when(userRepository.save(existing)).thenReturn(existing);

        userService.changePassword(ID, "OldP@ss1!", "NewP@ssw0rd");

        // verify encoded value was set and persisted
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("{argon2id}ENC_NEW", captor.getValue().getPassword());

        verify(passwordEncoder).matches("OldP@ss1!", "ENC_OLD");
        verify(passwordEncoder).encode("NewP@ssw0rd");
    }

    @Test
    void changePassword_wrongCurrent_throws() {
        final long ID = 7L;
        User existing = User.builder().id(ID).password("ENC_OLD").build();

        when(userRepository.findById(ID)).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("wrong", "ENC_OLD")).thenReturn(false);

        assertThrows(UserPasswordValidationException.class,
                () -> userService.changePassword(ID, "wrong", "NewP@ssw0rd"));

        verify(passwordEncoder).matches("wrong", "ENC_OLD");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_notFound_throws() {
        final long ID = 77L;
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.changePassword(ID, "OldP@ss1!", "NewP@ssw0rd"));

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_rejects_weakPassword_doesNotEncodeOrSave() {
        final long ID = 1L;
        User existing = User.builder().id(ID).password("ENC_OLD").build();

        when(userRepository.findById(ID)).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("OldP@ss1!", "ENC_OLD")).thenReturn(true);

        // weak new password -> should fail validation in service
        assertThrows(UserPasswordValidationException.class,
                () -> userService.changePassword(ID, "OldP@ss1!", "short1!"));

        verify(passwordEncoder).matches("OldP@ss1!", "ENC_OLD");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

}
