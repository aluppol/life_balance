package com.luppol.life_balance.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.luppol.life_balance.dto.UserCreateDto;
import com.luppol.life_balance.dto.UserPatchDto;
import com.luppol.life_balance.dto.UserPutDto;
import com.luppol.life_balance.dto.UserReadDto;
import com.luppol.life_balance.exceptions.NotFoundException;

import java.util.List;

public class UserService implements CrudService<Long, UserCreateDto, UserReadDto, UserPutDto, UserPatchDto>{
    @Override
    public UserReadDto create(UserCreateDto userCreateDto) {
        return null;
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

    public void changePassword(Long id, String oldPass, String newPass) {
    }
}
