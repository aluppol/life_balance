package com.luppol.life_balance.services;


import com.luppol.life_balance.exceptions.NotFoundException;

import java.util.List;

/**
 * Generic contract for basic CRUD operations.
 *
 * @param <E>  Entity type
 * @param <ID> Identifier type
 * @param <Dto> Dto record for entity
 */
public interface CrudService<E, ID, Dto> {
    E create(Dto dto);
    E getById(ID id) throws NotFoundException;
    List<E> getAll();
    E update(ID id, Dto dto) throws NotFoundException;
    E patch(ID id, Dto dto) throws NotFoundException;
    void deleteById(ID id) throws NotFoundException;
    long count();
}
