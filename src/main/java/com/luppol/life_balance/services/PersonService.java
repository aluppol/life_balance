package com.luppol.life_balance.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.luppol.life_balance.dto.PersonCreateDto;
import com.luppol.life_balance.dto.PersonPatchDto;
import com.luppol.life_balance.dto.PersonPutDto;
import com.luppol.life_balance.dto.PersonReadDto;
import com.luppol.life_balance.exceptions.DuplicatePersonException;
import com.luppol.life_balance.exceptions.NotFoundException;
import com.luppol.life_balance.mappers.PersonMapper;
import com.luppol.life_balance.models.Person;
import com.luppol.life_balance.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonService implements CrudService<Long, PersonCreateDto, PersonReadDto, PersonPutDto, PersonPatchDto> {
    private final PersonRepository personRepo;
    private final PersonMapper personMapper;

    @Override
    public PersonReadDto create(PersonCreateDto dto) {
        Person person = personMapper.toPerson(dto);
        if (personRepo.existsByFirstNameAndLastName(person.getFirstName(), person.getLastName())) {
            throw new DuplicatePersonException("Duplicate Person");
        }
        if (personRepo.existsByPhoneNumber(person.getPhoneNumber())) {
            throw new DuplicatePersonException("Duplicate phone number");
        }
        return personMapper.toReadDto(personRepo.save(person));
    }

    @Override
    public PersonReadDto getById(Long id) {
        return personMapper.toReadDto(getPersonById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonReadDto> getAll() {
        return personRepo.findAll().stream().map(personMapper::toReadDto).toList();
    }

    @Override
    public PersonReadDto put(Long id, PersonPutDto dto) {
        Person person = getPersonById(id);
        personMapper.putFromDtoToPerson(dto, person);
        return personMapper.toReadDto(personRepo.save(person));
    }

    @Override
    public PersonReadDto patch(Long id, PersonPatchDto dto, JsonNode jsonBody) {
        Person person = getPersonById(id);
        personMapper.patchFromDtoToPerson(dto, jsonBody, person);
        return personMapper.toReadDto(personRepo.save(person));
    }

    @Override
    public void deleteById(Long id) {
        personRepo.deleteById(id);
    }

    @Override
    public long count() {
        return personRepo.count();
    }

    @Transactional(readOnly = true)
    private Person getPersonById(Long id) {
        return personRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Person with id %d not found!", id)));
    }
}
