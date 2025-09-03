package com.luppol.life_balance.auth.services;

import com.luppol.life_balance.auth.dto.*;

public interface IAuthService {
    void register(AuthRegisterDto body);
    AuthTokensDto login(AuthLoginDto body);
    void changePassword(long userId, AuthPasswordChangeDto body);
    void changeEmail(long userId, AuthEmailChangeDto body);
    void changeUsername(long userId, String username);


    void logout(String refreshToken);
    AuthTokensDto refresh(String refreshToken);
}
