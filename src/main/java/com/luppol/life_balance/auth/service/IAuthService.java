package com.luppol.life_balance.auth.service;

import com.luppol.life_balance.auth.dto.*;
import com.luppol.life_balance.dto.PersonReadDto;

public interface IAuthService {
    void register(AuthRegisterDto body);
    AuthTokensDto login(AuthLoginDto body);
    void changePassword(long userId, AuthPasswordChangeDto body);
    void logout(String refreshToken);
    AuthTokensDto refresh(String refreshToken);
    UserReadDto me(long userId);
}
