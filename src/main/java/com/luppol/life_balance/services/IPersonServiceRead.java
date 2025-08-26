package com.luppol.life_balance.services;

import com.luppol.life_balance.dto.PersonReadDto;

public interface IPersonServiceRead {
    PersonReadDto getById(Long id);

}
