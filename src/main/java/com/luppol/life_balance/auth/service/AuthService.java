package com.luppol.life_balance.auth.service;

import com.luppol.life_balance.auth.dto.*;
import com.luppol.life_balance.dto.PersonReadDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService implements IAuthService{
    private final IUserService userService;

    @Override
    public AuthDto register(UserCreateDto registerDto) {
        UserReadDto user = userService.create(registerDto);
    }

    @Override
    public AuthDto login(LoginDto body) {
        return null;
    }

    @Override
    public AuthDto changePassword(long userId, PasswordChangeDto body) {
        return null;
    }

    @Override
    public void logout(String refreshToken) {

    }

    @Override
    public AuthDto refresh(String refreshToken) {
        return null;
    }

    @Override
    public PersonReadDto me(long personId) {
        return null;
    }
}
