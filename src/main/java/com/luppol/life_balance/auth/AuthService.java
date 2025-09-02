package com.luppol.life_balance.auth;

import com.luppol.life_balance.auth.mappers.AuthMapper;
import com.luppol.life_balance.auth.dto.*;
import com.luppol.life_balance.auth.service.IAuthService;
import com.luppol.life_balance.auth.service.IUserService;
import com.luppol.life_balance.exceptions.UserPasswordValidationException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthService implements IAuthService {
    private final AuthMapper authMapper;
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(AuthRegisterDto registerDto) {
        validatePassword(registerDto.password());
        UserCreateDto user = authMapper.toUserCreateDto(registerDto, passwordEncoder.encode(registerDto.password()));
        userService.create(user);
    }

    @Override
    public AuthTokensDto login(AuthLoginDto body) {
        return null;
    }

    @Override
    public void changePassword(long userId, AuthPasswordChangeDto body) {
    }

    @Override
    public void logout(String refreshToken) {

    }

    @Override
    public AuthTokensDto refresh(String refreshToken) {
        return null;
    }

    @Override
    public UserReadDto me(long userId) {
        return null;
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
