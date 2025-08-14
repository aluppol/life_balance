package com.luppol.life_balance.services;

import com.luppol.life_balance.dto.UserCreateDto;
import com.luppol.life_balance.dto.UserPatchDto;
import com.luppol.life_balance.dto.UserPutDto;
import com.luppol.life_balance.dto.UserReadDto;

public interface IUserService extends CrudService<Long, UserCreateDto, UserReadDto, UserPutDto, UserPatchDto> {
    void changePassword(Long id, String oldPass, String newPass);
}
