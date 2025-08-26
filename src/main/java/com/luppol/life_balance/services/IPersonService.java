package com.luppol.life_balance.services;

import com.luppol.life_balance.dto.PersonCreateDto;
import com.luppol.life_balance.dto.PersonPatchDto;
import com.luppol.life_balance.dto.PersonPutDto;
import com.luppol.life_balance.dto.PersonReadDto;

public interface IPersonService extends CrudService<Long, PersonCreateDto, PersonReadDto, PersonPutDto, PersonPatchDto>, IPersonServiceRead { }
