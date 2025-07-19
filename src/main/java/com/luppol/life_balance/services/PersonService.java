package com.luppol.life_balance.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.luppol.life_balance.dto.*;
import com.luppol.life_balance.exceptions.DuplicatePersonException;
import com.luppol.life_balance.mappers.PersonMapper;
import com.luppol.life_balance.models.Person;
import com.luppol.life_balance.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService implements CrudService<Long, PersonCreateDto, PersonReadDto, PersonPutDto, PersonPatchDto> {
    private final PersonRepository personRepo;
    private final PersonMapper personMapper;

    @Override
    @Transactional
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
    @Transactional(readOnly = true)
    public PersonReadDto getById(Long id) {
        return personMapper.toReadDto(personRepo.findRequired(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonReadDto> getAll() {
        return personRepo.findAll().stream().map(personMapper::toReadDto).toList();
    }

    @Override
    @Transactional
    public PersonReadDto put(Long id, PersonPutDto dto) {
        Person person = personRepo.findRequired(id);
        personMapper.putFromDtoToPerson(dto, person);
        return personMapper.toReadDto(personRepo.save(person));
    }

    @Override
    @Transactional
    public PersonReadDto patch(Long id, PersonPatchDto dto, JsonNode jsonBody) {
        Person person = personRepo.findRequired(id);
        personMapper.patchFromDtoToPerson(dto, jsonBody, person);
        return personMapper.toReadDto(personRepo.save(person));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        personRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return personRepo.count();
    }
}
