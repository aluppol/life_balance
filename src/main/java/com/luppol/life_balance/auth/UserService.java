package com.luppol.life_balance.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.luppol.life_balance.auth.dto.UserCreateDto;
import com.luppol.life_balance.auth.dto.UserPatchDto;
import com.luppol.life_balance.auth.dto.UserPutDto;
import com.luppol.life_balance.auth.dto.UserReadDto;
import com.luppol.life_balance.auth.mappers.UserMapper;
import com.luppol.life_balance.auth.service.IUserService;
import com.luppol.life_balance.exceptions.*;
import com.luppol.life_balance.auth.moderls.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserReadDto create(UserCreateDto userCreateDto) {
        if (!isEmailAvailable(userCreateDto.email())) {
            throw new DuplicateUserException("User with such email already exists, please use login");
        }

        if (!isUsernameAvailable(userCreateDto.username())) {
            throw new DuplicateUserException("User with such username already exists, please use login");
        }

        User user = userMapper.toUser(userCreateDto);

        return userMapper.toReadDto(userRepository.save(user));
    }

    @Override
    public UserReadDto getById(Long id) throws NotFoundException {
        return userMapper.toReadDto(userRepository.findRequired(id));
    }

    @Override
    public UserReadDto put(Long id, UserPutDto userPutDto) throws NotFoundException {
        User user = userRepository.findRequired(id);
        userMapper.putFromDtoToUser(userPutDto, user);

        if (!isEmailAvailable(id, user.getEmail())) {
            throw new UserEmailValidationException("User with such email already exists!");
        }

        if (!isUsernameAvailable(id, user.getUsername())) {
            throw new UserUsernamelValidationException("User with such username already exists!");
        }

        return userMapper.toReadDto(userRepository.save(user));
    }

    @Override
    public UserReadDto patch(Long id, UserPatchDto userPatchDto, JsonNode json) throws NotFoundException {
        User user = userRepository.findRequired(id);
        userMapper.patchFromDtoToUser(userPatchDto, json, user);

        if (!isEmailAvailable(id, user.getEmail())) {
            throw new UserEmailValidationException("User with such email already exists!");
        }

        if (!isUsernameAvailable(id, user.getUsername())) {
            throw new UserUsernamelValidationException("User with such username already exists!");
        }

        return userMapper.toReadDto(userRepository.save(user));
    }

    @Override
    public void deleteById(Long id) throws NotFoundException {
        userRepository.deleteById(id);
    }


    private boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    private boolean isEmailAvailable(Long id, String email) {
        return userRepository.findByEmail(email)
                .map(user -> id.equals(user.getId()))
                .orElse(true);
    }

    private boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    private boolean isUsernameAvailable(Long id, String username) {
        return userRepository.findByUsername(username)
                .map(user -> id.equals(user.getId()))
                .orElse(true);
    }
}
