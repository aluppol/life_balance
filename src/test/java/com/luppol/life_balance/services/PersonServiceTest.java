package com.luppol.life_balance.services;

import com.luppol.life_balance.dto.PersonDto;
import com.luppol.life_balance.exceptions.NotFoundException;
import com.luppol.life_balance.mappers.PersonMapper;
import com.luppol.life_balance.models.Person;
import com.luppol.life_balance.repositories.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {
    @Mock
    PersonRepository repo;

    @Mock
    PersonMapper mapper;

    @InjectMocks
    PersonService service;

    @Test
    void create_rejectDuplicate() {
        PersonDto dto = new PersonDto(null, "Bob", "Lee", null,null, null, null);
        Person entity = Person.builder().firstName("Bob").lastName("Lee").build();
        when(repo.existsByFirstNameAndLastName("Bob", "Lee")).thenReturn(true);
        when(mapper.toEntity(dto)).thenReturn(entity);
        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
    }

    @Test
    void create_succeeds() {
        PersonDto dto = new PersonDto(null, "A", "B", null,null, null, null);
        Person entity = Person.builder().firstName("A").lastName("B").build();
        when(repo.existsByFirstNameAndLastName("A", "B")).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(entity);
        assertEquals(entity, service.create(dto));
    }

    @Test
    void getById_notFound() {
        final long ID = 1L;
        when(repo.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getById(ID));
    }

    @Test
    void getById_succeeds() {
        final long ID = 1L;
        Person entity = new Person();
        entity.setId(ID);
        when(repo.findById(ID)).thenReturn(Optional.of(entity));
        assertEquals(entity, service.getById(ID));
    }

    @Test
    void getAll() {
        when(repo.findAll()).thenReturn(List.of(new Person(), new Person()));
        assertEquals(2, service.getAll().size());
    }

    @Test
    void update_notFound() {
        final long ID = 1L;
        when(repo.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.update(ID, new PersonDto(null, "", "", null, null, null, null)));
    }

    @Test
    void update_succeeds() {
        final long ID = 1L;
        Person old = new Person();
        old.setId(ID);
        PersonDto dto = new PersonDto(null, "X", "Y", null, null, null, null);
        Person updated = Person.builder().firstName("X").lastName("Y").build();
        Person saved = Person.builder().firstName("X").lastName("Y").id(ID).build();
        when(repo.findById(ID)).thenReturn(Optional.of(old));
        when(mapper.toEntity(dto)).thenReturn(updated);
        when(repo.save(saved)).thenReturn(saved);
        assertEquals(saved, service.update(ID, dto));
        verify(repo).save(saved);
    }

    @Test
    void patch_notFound() {
        final long ID = 1L;
        when(repo.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.patch(ID, new PersonDto(null, "", "", null, null, null, null)));
    }


    //TODO fix patch test
//    @Test
//    void patch_succeeds() {
//        final long ID = 1L;
//        final String FIRST_NAME = "X";
//        final String LAST_NAME = "Y";
//
//        Person old = new Person();
//        old.setId(ID);
//        old.setMiddleName("Deleted");
//
//        PersonDto dto = new PersonDto(null, FIRST_NAME, LAST_NAME, null, null, null, null);
//
//        Person expected = Person.builder().firstName(FIRST_NAME).lastName(LAST_NAME).id(ID).middleName(null).build();
//
//        when(repo.findById(ID)).thenReturn(Optional.of(old));
//        when(repo.save(any(Person.class))).thenReturn(expected);
//
//       service.patch(ID, dto);
//
//        verify(mapper).merge(dto, old);
//
//        ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
//        verify(repo).save(personCaptor.capture());
//        Person saved = personCaptor.getValue();
//
//        assertEquals(ID, saved.getId());
//        assertEquals(FIRST_NAME, saved.getFirstName());
//        assertEquals(LAST_NAME, saved.getLastName());
//        assertNull(saved.getMiddleName());
//    }

    @Test
    void deleteById_callsRepo() {
        final long ID = 1L;
        service.deleteById(ID);
        verify(repo).deleteById(ID);
    }

    @Test
    void count_returnsRepoCount() {
        final long COUNT = 10L;
        when(repo.count()).thenReturn(COUNT);
        assertEquals(COUNT, service.count());
    }
}
