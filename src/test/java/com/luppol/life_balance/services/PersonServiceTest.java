package com.luppol.life_balance.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luppol.life_balance.dto.PersonCreateDto;
import com.luppol.life_balance.dto.PersonPatchDto;
import com.luppol.life_balance.dto.PersonPutDto;
import com.luppol.life_balance.dto.PersonReadDto;
import com.luppol.life_balance.exceptions.NotFoundException;
import com.luppol.life_balance.mappers.PersonMapper;
import com.luppol.life_balance.models.Person;
import com.luppol.life_balance.repositories.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    PersonRepository repo;

    @Mock
    PersonMapper mapper;

    @InjectMocks
    PersonService service;

    @Test
    void create_rejectDuplicate() {
        PersonCreateDto dto = new PersonCreateDto("Bob", "Lee", null,null, null);
        Person person = Person.builder().firstName("Bob").lastName("Lee").build();
        when(repo.existsByFirstNameAndLastName("Bob", "Lee")).thenReturn(true);
        when(mapper.toPerson(dto)).thenReturn(person);
        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
    }

    @Test
    void create_succeeds() {
        PersonCreateDto createDto = new PersonCreateDto(
                "A", "B",null, null, null
        );
        PersonReadDto readDto = new PersonReadDto(
                1L, "A", "B", null, null, null, null
        );
        Person personToSave = Person.builder().firstName("A").lastName("B").build();
        Person personSaved = Person.builder().id(1L).firstName("A").lastName("B").build();

        when(mapper.toPerson(createDto)).thenReturn(personToSave);
        when(mapper.toReadDto(personSaved)).thenReturn(readDto);

        when(repo.existsByFirstNameAndLastName("A", "B")).thenReturn(false);
        when(repo.save(personToSave)).thenReturn(personSaved);

        assertEquals(mapper.toReadDto(personSaved), service.create(createDto));
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
        Person person = Person.builder().id(ID).build();
        PersonReadDto readDto = new PersonReadDto(
                1L, null, null, null, null, null, null
        );

        when(repo.findById(ID)).thenReturn(Optional.of(person));
        when(mapper.toReadDto(person)).thenReturn(readDto);

        assertEquals(readDto, service.getById(ID));
    }

    @Test
    void getAll() {
        when(repo.findAll()).thenReturn(List.of(new Person(), new Person()));
        when(mapper.toReadDto(new Person())).thenReturn(new PersonReadDto(
                null, null, null, null, null, null, null
        ));
        assertEquals(2, service.getAll().size());
    }

    @Test
    void put_notFound() {
        final long ID = 1L;
        when(repo.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.put(ID, new PersonPutDto(
                "", "", null, null, null
        )));
    }

    @Test
    void put_succeeds() {
        final long ID = 1L;
        Person personOld = Person.builder().id(ID).build();
        Person personUpdated = Person.builder().id(ID).firstName("X").lastName("Y").build();
        PersonPutDto putDto = new PersonPutDto(
                "X", "Y", null, null, null
        );
        PersonReadDto readDto = new PersonReadDto(
                1L, "X",  "Y", null, null, null, null
        );

        when(repo.findById(ID)).thenReturn(Optional.of(personOld));
        when(repo.save(personOld)).thenReturn(personUpdated);

        when(mapper.toReadDto(personUpdated)).thenReturn(readDto);
        doAnswer(putPersonFromDtoInvocation -> {
            PersonPutDto dto = putPersonFromDtoInvocation.getArgument(0);
            Person person = putPersonFromDtoInvocation.getArgument(1);

            person.setFirstName(dto.firstName());
            person.setLastName(dto.lastName());

            return null;
        }).when(mapper).putFromDtoToPerson(eq(putDto), eq(personOld));

        assertEquals(readDto, service.put(ID, putDto));

        verify(repo).save(personUpdated);
        verify(mapper).putFromDtoToPerson(putDto, personOld);
    }

    @Test
    void patch_notFound() {
        final long ID = 1L;

        when(repo.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.patch(
                ID,
                new PersonPatchDto(null, null, null, null, null),
                objectMapper.createObjectNode()
        ));
    }

    @Test
    void patch_succeeds() {
        final long ID = 1L;
        final String FIRST_NAME = "X";
        final String LAST_NAME = "Y";
        final String OLD_ADDRESS = "Old Address";
        final String NEW_ADDRESS = "New Address";

        JsonNode jsonBody = objectMapper.createObjectNode()
                .put("address", NEW_ADDRESS)
                .putNull("middleName");
        Person personOld = Person.builder()
                .id(ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .middleName("Deleted")
                .address(OLD_ADDRESS)
                .build();
        Person personPatched = Person.builder()
                .id(ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .address(NEW_ADDRESS)
                .build();

        PersonPatchDto patchDto = new PersonPatchDto(null, null, null, null, NEW_ADDRESS);
        PersonReadDto readDto = new PersonReadDto(
                ID, FIRST_NAME, LAST_NAME, null, null, NEW_ADDRESS, null
        );

        when(repo.findById(ID)).thenReturn(Optional.of(personOld));
        when(repo.save(personOld)).thenReturn(personPatched);

        when(mapper.toReadDto(personPatched)).thenReturn(readDto);
        doAnswer(patchPersonFromDtoInvocation -> {
            PersonPatchDto dto = patchPersonFromDtoInvocation.getArgument(0);
            JsonNode json = patchPersonFromDtoInvocation.getArgument(1);
            Person person = patchPersonFromDtoInvocation.getArgument(2);

            if (json.has("address")) {
                person.setAddress(dto.address());
            }

            if (json.has("middleName")) {
                person.setMiddleName(dto.middleName());
            }

            return null;
        }).when(mapper).patchFromDtoToPerson(eq(patchDto), eq(jsonBody), eq(personOld));

        assertEquals(readDto, service.patch(ID, patchDto, jsonBody));

        verify(mapper).patchFromDtoToPerson(patchDto, jsonBody, personOld);
        verify(mapper).toReadDto(personPatched);
        verify(repo).findById(ID);
        verify(repo).save(personOld);
    }

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
