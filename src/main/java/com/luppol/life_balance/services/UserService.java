package com.luppol.life_balance.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.luppol.life_balance.dto.UserCreateDto;
import com.luppol.life_balance.dto.UserPatchDto;
import com.luppol.life_balance.dto.UserPutDto;
import com.luppol.life_balance.dto.UserReadDto;
import com.luppol.life_balance.exceptions.DuplicateUserException;
import com.luppol.life_balance.exceptions.NotFoundException;
import com.luppol.life_balance.exceptions.UserEmailValidationException;
import com.luppol.life_balance.exceptions.UserPasswordValidationException;
import com.luppol.life_balance.mappers.UserMapper;
import com.luppol.life_balance.models.User;
import com.luppol.life_balance.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeSourceSetImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserReadDto create(UserCreateDto userCreateDto) {
        User user = userMapper.toUser(userCreateDto);

        validatePassword(user.getPassword());
        validateEmail(user.getEmail());

        if (user.getUsername() == null) {
            user.setUsername(user.getEmail());
        }

        validateUsername(user.getUsername());

        return userMapper.toReadDto(userRepository.save(user));
    }

    @Override
    public UserReadDto getById(Long aLong) throws NotFoundException {
        return null;
    }

    @Override
    public List<UserReadDto> getAll() {
        return List.of();
    }

    @Override
    public UserReadDto put(Long aLong, UserPutDto userPutDto) throws NotFoundException {
        return null;
    }

    @Override
    public UserReadDto patch(Long aLong, UserPatchDto userPatchDto, JsonNode json) throws NotFoundException {
        return null;
    }

    @Override
    public void deleteById(Long aLong) throws NotFoundException {

    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void changePassword(Long id, String oldPass, String newPass) {
    }

    private void validateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUserException("User with such username already exists, please use login");
        }
    }

    private void validateEmail(String email) {
        final String EMAIL_REGEX =
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new UserEmailValidationException("Not matches the email pattern");
        }

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateUserException("User with such email already exists, please use login");
        }
    }

    private void validatePassword(String pass) throws UserPasswordValidationException {
        List<String> validationResults = new ArrayList<>();

        validationResults.add(validatePasswordLength(pass));
        validationResults.add(validatePasswordNoQwerty(pass));
        validationResults.add(validatePasswordHasDigit(pass));
        validationResults.add(validatePasswordHasLowercase(pass));
        validationResults.add(validatePasswordHasUppercase(pass));
        validationResults.add(validatePasswordHasSpecial(pass));

        List<String> errors = validationResults.stream().filter(e -> !e.isEmpty()).toList();

        if (!errors.isEmpty()) {
            throw new UserPasswordValidationException(errors);
        }
    }

    private String validatePasswordLength(String pass) {
        final int MIN_LENGTH = 8;
        final int MAX_LENGTH = 30;

        if (pass.length() < MIN_LENGTH || pass.length() > MAX_LENGTH) {
            return String.format("Incorrect length, must be between %d and %d", MIN_LENGTH, MAX_LENGTH);
        }

        return "";
    }

    private String validatePasswordHasDigit(String pass) {
        boolean hasDigit = pass.chars().anyMatch(Character::isDigit);
        return hasDigit ? "" : "Must include at least one digit [0-9]";
    }

    private String validatePasswordHasSpecial(String pass) {
        boolean hasSpecial = pass.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));
        return hasSpecial ? "" : "Must include at least one special character";
    }

    private String validatePasswordHasLowercase(String pass) {
        boolean hasLower = pass.chars().anyMatch(Character::isLowerCase);
        return hasLower ? "" : "Must include at least one lowercase letter [a-z]";
    }

    private String validatePasswordHasUppercase(String pass) {
        boolean hasUpper = pass.chars().anyMatch(Character::isUpperCase);
        return hasUpper ? "" : "Must include at least one uppercase letter [A-Z]";
    }

    private String validatePasswordNoQwerty(String pass) {
        String lower = pass.toLowerCase(java.util.Locale.ROOT);
        return lower.contains("qwerty") ? "Must not contain the insecure sequence 'qwerty'" : "";
    }
}
