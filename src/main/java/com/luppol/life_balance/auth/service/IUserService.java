package com.luppol.life_balance.auth.service;

import com.luppol.life_balance.auth.dto.UserCreateDto;
import com.luppol.life_balance.auth.dto.UserPatchDto;
import com.luppol.life_balance.auth.dto.UserPutDto;
import com.luppol.life_balance.auth.dto.UserReadDto;
import com.luppol.life_balance.services.CrudService;

public interface IUserService extends CrudService<Long, UserCreateDto, UserReadDto, UserPutDto, UserPatchDto> {
    void changePassword(Long id, String oldPass, String newPass);
}
