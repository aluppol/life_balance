package com.luppol.life_balance.services;

import com.luppol.life_balance.dto.PersonDto;
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
public class PersonService implements CrudService<Person, Long, PersonDto> {
    private final PersonRepository personRepo;
    private final PersonMapper personMapper;

    @Override
    public Person create(PersonDto dto) {
        Person person = personMapper.toEntity(dto);
        if (personRepo.existsByFirstNameAndLastName(person.getFirstName(), person.getLastName())) {
            throw new IllegalArgumentException("Duplicate Person");
        }
        return personRepo.save(person);
    }

    @Override
    @Transactional(readOnly = true)
    public Person getById(Long id) {
        return personRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Person with id %d not found!", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> getAll() {
        return personRepo.findAll();
    }

    @Override
    public Person update(Long id, PersonDto dto) {
        Person existing = getById(id);
        Person updated = personMapper.toEntity(dto);
        updated.setId(existing.getId());
        return personRepo.save(updated);
    }

    @Override
    public Person patch(Long id, PersonDto dto) {
        Person existing = getById(id);
        personMapper.merge(dto, existing);
        return personRepo.save(existing);
    }

    @Override
    public void deleteById(Long id) {
        personRepo.deleteById(id);
    }

    @Override
    public long count() {
        return personRepo.count();
    }
}
