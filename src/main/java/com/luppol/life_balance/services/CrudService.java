package com.luppol.life_balance.services;


import com.luppol.life_balance.exceptions.NotFoundException;

import java.util.List;

/**
 * Generic contract for basic CRUD operations.
 *
 * @param <ReadDto>  Read dto record
 * @param <ID> Identifier type
 * @param <CreateDto>  Read dto record
 * @param <PutDto>  Read dto record
 * @param <PatchDto>  Read dto record
 */
public interface CrudService<ID, CreateDto, ReadDto, PutDto, PatchDto> {
    ReadDto create(CreateDto dto);
    ReadDto getById(ID id) throws NotFoundException;
    List<ReadDto> getAll();
    ReadDto put(ID id, PutDto dto) throws NotFoundException;
    ReadDto patch(ID id, PatchDto dto) throws NotFoundException;
    void deleteById(ID id) throws NotFoundException;
    long count();
}
