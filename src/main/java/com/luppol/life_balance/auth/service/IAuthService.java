package com.luppol.life_balance.auth.service;

import com.luppol.life_balance.auth.dto.AuthDto;
import com.luppol.life_balance.auth.dto.PasswordChangeDto;
import com.luppol.life_balance.auth.dto.LoginDto;
import com.luppol.life_balance.auth.dto.UserCreateDto;
import com.luppol.life_balance.dto.PersonReadDto;

public interface IAuthService {
    AuthDto register(UserCreateDto body);
    AuthDto login(LoginDto body);
    AuthDto changePassword(long userId, PasswordChangeDto body);
    void logout(String refreshToken);
    AuthDto refresh(String refreshToken);
    PersonReadDto me(long personId);
}
