package com.luppol.life_balance.auth.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.luppol.life_balance.auth.mappers.AuthMapper;
import com.luppol.life_balance.auth.dto.*;
import com.luppol.life_balance.auth.models.User;
import com.luppol.life_balance.exceptions.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthService implements IAuthService {
    private final AuthMapper authMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void register(AuthRegisterDto dto) {
        validatePassword(dto.password());

        User user = authMapper.toUser(dto, passwordEncoder.encode(dto.password()));

        if (userRepository.existsByUsername(user.getUsername())) throw new DuplicateUserException("Username already exists");
        if (userRepository.existsByEmail(user.getEmail())) throw new DuplicateUserException("Email already exists");

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthTokensDto login(AuthLoginDto dto) {
        String username = dto.username().trim().toLowerCase();
        User user = userRepository.findByUsername(username).orElseGet(() -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username \"" + username + "\" is not found!")));
        if (!passwordEncoder.matches(dto.password(), user.getPassword())) throw new IllegalArgumentException("Bad password");

        throw new UnsupportedOperationException("Token issuance not implemented yet");
    }

    @Override
    @Transactional
    public void changePassword(long userId, AuthPasswordChangeDto dto) {
        User user = userRepository.findRequired(userId);
        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) throw new IllegalArgumentException("Incorrect Password");
        int rows = userRepository.updatePassword(userId, passwordEncoder.encode(dto.newPassword()));
        if (rows != 1) throw new IllegalStateException("Password not updated");
    }

    @Override
    @Transactional
    public void changeEmail(long userId, AuthEmailChangeDto dto) {
        User user = userRepository.findRequired(userId);

        if (user.getEmail().equals(dto.email())) return;

        if (userRepository.existsByEmail(dto.email())) throw new IllegalArgumentException("Email already exists");
        if (!passwordEncoder.matches(dto.password(), user.getPassword())) throw new IllegalArgumentException("Incorrect Password");
        int rows = userRepository.updateEmail(userId, dto.email().trim().toLowerCase());
        if (rows != 1) throw new IllegalStateException("Email Not Updated");
    }

    @Override
    @Transactional
    public void changeUsername(long userId, String usernameIn) {
        String newUsername = authMapper.normalize(usernameIn);

        User user = userRepository.findRequired(userId);

        if (user.getUsername().equals(newUsername)) return;

        if (userRepository.existsByUsername(newUsername.trim().toLowerCase())) throw new IllegalArgumentException("Username already exists");

        user.setUsername(newUsername);
        userRepository.save(user);
    }

    @Override
    public void logout(String refreshToken) {

    }

    @Override
    public AuthTokensDto refresh(String refreshToken) {
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
