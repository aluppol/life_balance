package com.luppol.life_balance.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luppol.life_balance.dto.PersonCreateDto;
import com.luppol.life_balance.dto.PersonPatchDto;
import com.luppol.life_balance.dto.PersonPutDto;
import com.luppol.life_balance.dto.PersonReadDto;
import com.luppol.life_balance.exceptions.DuplicatePersonException;
import com.luppol.life_balance.exceptions.NotFoundException;
import com.luppol.life_balance.mappers.PersonMapper;
import com.luppol.life_balance.models.Person;
import com.luppol.life_balance.repositories.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
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


    @Mock(answer = Answers.CALLS_REAL_METHODS) PersonRepository personRepository;
    @Mock PersonMapper personMapper;

    @InjectMocks
    PersonService personService;

    @Test
    void create_rejectDuplicateName_throws() {
        PersonCreateDto dto = new PersonCreateDto("Bob", "Lee", "bob.lee@gmail.com", null,null, null);
        Person person = Person.builder().firstName("Bob").lastName("Lee").build();
        when(personRepository.existsByFirstNameAndLastName("Bob", "Lee")).thenReturn(true);
        when(personMapper.toPerson(dto)).thenReturn(person);
        assertThrows(DuplicatePersonException.class, () -> personService.create(dto));
    }

    @Test
    void create_rejectDuplicatePhone_throws() {
        PersonCreateDto dto = new PersonCreateDto("Bob", "Lee", "bob.lee@gmail.com", null,"45", null);
        Person person = Person.builder().firstName("Bob").lastName("Jee").phoneNumber("45").build();
        when(personRepository.existsByPhoneNumber("45")).thenReturn(true);
        when(personMapper.toPerson(dto)).thenReturn(person);
        assertThrows(DuplicatePersonException.class, () -> personService.create(dto));
    }

    @Test
    void create_succeeds() {
        PersonCreateDto createDto = new PersonCreateDto(
                "A", "B", "bob.lee@gmail.com",null, null, null
        );
        PersonReadDto readDto = new PersonReadDto(
                1L, "A", "B", "bob.lee@gmail.com", null, null, null, null
        );
        Person personToSave = Person.builder().firstName("A").lastName("B").email("bob.lee@gmail.com").build();
        Person personSaved = Person.builder().id(1L).firstName("A").lastName("B").email("bob.lee@gmail.com").build();

        when(personMapper.toPerson(createDto)).thenReturn(personToSave);
        when(personMapper.toReadDto(personSaved)).thenReturn(readDto);

        when(personRepository.existsByFirstNameAndLastName("A", "B")).thenReturn(false);
        when(personRepository.save(personToSave)).thenReturn(personSaved);

        assertEquals(readDto, personService.create(createDto));
    }

    @Test
    void getById_notFound_throws() {
        final long ID = 1L;
        when(personRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> personService.getById(ID));
    }

    @Test
    void getById_succeeds() {
        final long ID = 1L;
        Person person = Person.builder().id(ID).build();
        PersonReadDto readDto = new PersonReadDto(
                1L, null, null, null, null, null, null, null
        );

        when(personRepository.findById(ID)).thenReturn(Optional.of(person));
        when(personMapper.toReadDto(person)).thenReturn(readDto);

        assertEquals(readDto, personService.getById(ID));
    }

    @Test
    void getAll() {
        when(personRepository.findAll()).thenReturn(List.of(new Person(), new Person()));
        when(personMapper.toReadDto(new Person())).thenReturn(new PersonReadDto(
                null, null, null, null, null, null, null, null
        ));
        assertEquals(2, personService.getAll().size());
    }

    @Test
    void put_notFound() {
        final long ID = 1L;
        when(personRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> personService.put(ID, new PersonPutDto(
                "", "", "", null, null, null
        )));
    }

    @Test
    void put_succeeds() {
        final long ID = 1L;
        Person personOld = Person.builder().id(ID).build();
        Person personUpdated = Person.builder().id(ID).firstName("X").lastName("Y").build();
        PersonPutDto putDto = new PersonPutDto(
                "X", "Y", "X.Y@gmail.com", null, null, null
        );
        PersonReadDto readDto = new PersonReadDto(
                1L, "X",  "Y", "X.Y@gmail.com", null, null, null, null
        );

        when(personRepository.findById(ID)).thenReturn(Optional.of(personOld));
        when(personRepository.save(personOld)).thenReturn(personUpdated);

        when(personMapper.toReadDto(personUpdated)).thenReturn(readDto);
        doAnswer(putPersonFromDtoInvocation -> {
            PersonPutDto dto = putPersonFromDtoInvocation.getArgument(0);
            Person person = putPersonFromDtoInvocation.getArgument(1);

            person.setFirstName(dto.firstName());
            person.setLastName(dto.lastName());

            return null;
        }).when(personMapper).putFromDtoToPerson(eq(putDto), eq(personOld));

        assertEquals(readDto, personService.put(ID, putDto));

        verify(personRepository).save(personUpdated);
        verify(personMapper).putFromDtoToPerson(putDto, personOld);
    }

    @Test
    void patch_notFound() {
        final long ID = 1L;

        when(personRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> personService.patch(
                ID,
                new PersonPatchDto(null, null, null, null, null, null),
                objectMapper.createObjectNode()
        ));
    }

    @Test
    void patch_succeeds() {
        final long ID = 1L;
        final String FIRST_NAME = "X";
        final String LAST_NAME = "Y";
        final String EMAIL = "X.Y@gmail.com";
        final String OLD_ADDRESS = "Old Address";
        final String NEW_ADDRESS = "New Address";

        JsonNode jsonBody = objectMapper.createObjectNode()
                .put("address", NEW_ADDRESS)
                .putNull("middleName");
        Person personOld = Person.builder()
                .id(ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .middleName("Deleted")
                .address(OLD_ADDRESS)
                .build();
        Person personPatched = Person.builder()
                .id(ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .address(NEW_ADDRESS)
                .build();

        PersonPatchDto patchDto = new PersonPatchDto(null,  null,null, null, null, NEW_ADDRESS);
        PersonReadDto readDto = new PersonReadDto(
                ID, FIRST_NAME, LAST_NAME, EMAIL, null, null, NEW_ADDRESS, null
        );

        when(personRepository.findById(ID)).thenReturn(Optional.of(personOld));
        when(personRepository.save(personOld)).thenReturn(personPatched);

        when(personMapper.toReadDto(personPatched)).thenReturn(readDto);
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
        }).when(personMapper).patchFromDtoToPerson(eq(patchDto), eq(jsonBody), eq(personOld));

        assertEquals(readDto, personService.patch(ID, patchDto, jsonBody));

        verify(personMapper).patchFromDtoToPerson(patchDto, jsonBody, personOld);
        verify(personMapper).toReadDto(personPatched);
        verify(personRepository).findById(ID);
        verify(personRepository).save(personOld);
    }

    @Test
    void deleteById_callsRepo() {
        final long ID = 1L;
        personService.deleteById(ID);
        verify(personRepository).deleteById(ID);
    }

    @Test
    void count_returnsRepoCount() {
        final long COUNT = 10L;
        when(personRepository.count()).thenReturn(COUNT);
        assertEquals(COUNT, personService.count());
    }
}
