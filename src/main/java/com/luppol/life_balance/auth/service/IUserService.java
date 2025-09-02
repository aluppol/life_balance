package com.luppol.life_balance.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.luppol.life_balance.auth.dto.UserCreateDto;
import com.luppol.life_balance.auth.dto.UserPatchDto;
import com.luppol.life_balance.auth.dto.UserPutDto;
import com.luppol.life_balance.auth.dto.UserReadDto;
import com.luppol.life_balance.auth.moderls.User;
import com.luppol.life_balance.exceptions.DuplicateUserException;
import com.luppol.life_balance.exceptions.NotFoundException;
import com.luppol.life_balance.exceptions.UserEmailValidationException;
import com.luppol.life_balance.exceptions.UserUsernamelValidationException;
import com.luppol.life_balance.services.CrudService;

public interface IUserService {
    UserReadDto create(UserCreateDto userCreateDto);
    UserReadDto getById(Long id);
    UserReadDto put(Long id, UserPutDto userPutDto);
    UserReadDto patch(Long id, UserPatchDto userPatchDto, JsonNode json);
    void deleteById(Long id);
}
