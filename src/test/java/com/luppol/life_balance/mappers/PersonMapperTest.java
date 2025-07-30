package com.luppol.life_balance.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.luppol.life_balance.dto.PersonCreateDto;
import com.luppol.life_balance.dto.PersonPatchDto;
import com.luppol.life_balance.dto.PersonPutDto;
import com.luppol.life_balance.dto.PersonReadDto;
import com.luppol.life_balance.models.Mission;
import com.luppol.life_balance.models.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PersonMapperTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    PersonMapper mapper = Mappers.getMapper(PersonMapper.class);

    @Test
    void toReadDto_mapsAllFields() {
        Person person = Person.builder()
                .id(42L)
                .firstName("A")
                .lastName("B")
                .email("a.b@gmail.com")
                .middleName("C")
                .phoneNumber("123")
                .address("addr")
                .build();
        PersonReadDto dto = mapper.toReadDto(person);
        assertThat(dto.id()).isEqualTo(42L);
        assertThat(dto.firstName()).isEqualTo("A");
        assertThat(dto.lastName()).isEqualTo("B");
        assertThat(dto.email()).isEqualTo("a.b@gmail.com");
        assertThat(dto.middleName()).isEqualTo("C");
        assertThat(dto.phoneNumber()).isEqualTo("123");
        assertThat(dto.address()).isEqualTo("addr");
    }

    @Test
    void toPerson_mapsCreateDto() {
        PersonCreateDto dto = new PersonCreateDto("X", "Y", "x.y@gmail.com", "Z", "1234", "address");
        Person person = mapper.toPerson(dto);
        assertThat(person.getFirstName()).isEqualTo("X");
        assertThat(person.getLastName()).isEqualTo("Y");
        assertThat(person.getEmail()).isEqualTo("x.y@gmail.com");
        assertThat(person.getMiddleName()).isEqualTo("Z");
        assertThat(person.getPhoneNumber()).isEqualTo("1234");
        assertThat(person.getAddress()).isEqualTo("address");
    }

    @Test
    void putFromDtoToPerson_overwritesAllFields() {
        PersonPutDto putDto = new PersonPutDto("M", "N", "m.n@gmail.com", "O", "5678", "addr2");
        Person person = Person.builder().firstName("X").lastName("Y").middleName("Z").phoneNumber("0000").address("old").build();
        mapper.putFromDtoToPerson(putDto, person);
        assertThat(person.getFirstName()).isEqualTo("M");
        assertThat(person.getLastName()).isEqualTo("N");
        assertThat(person.getEmail()).isEqualTo("m.n@gmail.com");
        assertThat(person.getMiddleName()).isEqualTo("O");
        assertThat(person.getPhoneNumber()).isEqualTo("5678");
        assertThat(person.getAddress()).isEqualTo("addr2");
    }

    @Test
    void patchFromDtoToPerson_onlyPatchedFieldsAreChanged() {
        PersonPatchDto patchDto = new PersonPatchDto("Patched", null, null, null, null, null);
        ObjectNode json = objectMapper.createObjectNode();
        json.put("firstName", "Patched");
        Person person = Person.builder().firstName("Orig").lastName("Last").build();
        mapper.patchFromDtoToPerson(patchDto, json, person);
        assertThat(person.getFirstName()).isEqualTo("Patched");
        assertThat(person.getLastName()).isEqualTo("Last"); // not changed
    }


    @Test
    void patchFromDtoToPerson_updatesOnlyPresentFields() {
        PersonPatchDto dto = new PersonPatchDto("Alice", "Johnson", "alice.johnson@gmail.com", null, null, null);
        ObjectNode json = objectMapper.createObjectNode();
        json.put("firstName", "Alice");
        json.put("lastName", "Johnson");

        Person person = new Person();
        person.setFirstName("OldName");
        person.setLastName("OldLast");
        person.setEmail("alice.johnson@gmail.com");
        person.setMiddleName("M");
        person.setPhoneNumber("111");
        person.setAddress("Old Address");

        mapper.patchFromDtoToPerson(dto, json, person);

        assertEquals("Alice", person.getFirstName());
        assertEquals("Johnson", person.getLastName());
        assertEquals("alice.johnson@gmail.com", person.getEmail());
        assertEquals("M", person.getMiddleName()); // unchanged
        assertEquals("111", person.getPhoneNumber());
        assertEquals("Old Address", person.getAddress());
    }

    @Test
    void patchFromDtoToPerson_clearsFieldWhenExplicitlyNull() {
        PersonPatchDto dto = new PersonPatchDto(null, null,  null, null, null, null);
        ObjectNode json = objectMapper.createObjectNode();
        json.putNull("middleName");

        Person person = new Person();
        person.setFirstName("ToBeCleared");

        mapper.patchFromDtoToPerson(dto, json, person);

        assertNull(person.getMiddleName());
    }

    @Test
    void patchFromDtoToPerson_doesNothingIfFieldNotPresentInJson() {
        PersonPatchDto dto = new PersonPatchDto("ShouldNotApply", null, null,  null, null, null);
        ObjectNode json = objectMapper.createObjectNode();

        Person person = new Person();
        person.setFirstName("Original");

        mapper.patchFromDtoToPerson(dto, json, person);

        assertEquals("Original", person.getFirstName());
    }

    @Test
    void patchFromDtoToPerson_updatesAllFields() {
        PersonPatchDto dto = new PersonPatchDto("A", "B", "a.b@gmail.com", "C", "D", "E");
        ObjectNode json = objectMapper.createObjectNode();
        json.put("firstName", "A");
        json.put("lastName", "B");
        json.put("email", "a.b@gmail.com");
        json.put("middleName", "C");
        json.put("phoneNumber", "D");
        json.put("address", "E");

        Person person = new Person();
        mapper.patchFromDtoToPerson(dto, json, person);

        assertEquals("A", person.getFirstName());
        assertEquals("B", person.getLastName());
        assertEquals("C", person.getMiddleName());
        assertEquals("a.b@gmail.com", person.getEmail());
        assertEquals("D", person.getPhoneNumber());
        assertEquals("E", person.getAddress());
    }

    @Test
    void patchFromDtoToPerson_clearsAllFieldsWithExplicitNulls() {
        PersonPatchDto dto = new PersonPatchDto(null, null, null, null, null, null);
        ObjectNode json = objectMapper.createObjectNode();
        json.putNull("firstName");
        json.putNull("lastName");
        json.putNull("email");
        json.putNull("middleName");
        json.putNull("phoneNumber");
        json.putNull("address");

        Person person = new Person();
        person.setFirstName("X"); person.setLastName("Y"); person.setMiddleName("Z");
        person.setPhoneNumber("P"); person.setAddress("A");

        mapper.patchFromDtoToPerson(dto, json, person);

        assertNull(person.getFirstName());
        assertNull(person.getLastName());
        assertNull(person.getEmail());
        assertNull(person.getMiddleName());
        assertNull(person.getPhoneNumber());
        assertNull(person.getAddress());
    }

    @Test
    void patchFromDtoToPerson_doesNothingWhenNoFieldsPresent() {
        PersonPatchDto dto = new PersonPatchDto("newFirst", "newLast", "new.email@google.com", "newMid", "newPhone", "newAddr");
        ObjectNode json = objectMapper.createObjectNode();

        Person person = new Person();
        person.setFirstName("OldFirst");

        mapper.patchFromDtoToPerson(dto, json, person);

        assertEquals("OldFirst", person.getFirstName()); // unchanged
    }

    @Test
    void toReadDto_setsMissionIdWhenMissionPresent() {
        Person person = Person.builder()
                .id(1L)
                .firstName("John")
                .mission(Mission.builder().id(100L).build())
                .build();
        PersonReadDto dto = mapper.toReadDto(person);
        assertThat(dto.missionId()).isEqualTo(100L);
    }

    @Test
    void toReadDto_setsMissionIdNullWhenMissionAbsent() {
        Person person = Person.builder()
                .id(2L)
                .firstName("Jane")
                .mission(null)
                .build();
        PersonReadDto dto = mapper.toReadDto(person);
        assertThat(dto.missionId()).isNull();
    }

    @Test
    void toPerson_ignoresMissionOnCreate() {
        PersonCreateDto dto = new PersonCreateDto("X", "Y", "x.y@gmail.com", "Z", "1234", "address");
        Person person = mapper.toPerson(dto);
        assertThat(person.getMission()).isNull();
    }

    @Test
    void putFromDtoToPerson_doesNotOverwriteMission() {
        PersonPutDto dto = new PersonPutDto("A", "B", "a.b@gmail.com", "C", "D", "E");
        Person original = Person.builder()
                .mission(Mission.builder().id(42L).build())
                .build();
        mapper.putFromDtoToPerson(dto, original);
        assertThat(original.getMission()).isNotNull();
        assertThat(original.getMission().getId()).isEqualTo(42L);
    }

    @Test
    void patchFromDtoToPerson_doesNotAffectMission() {
        PersonPatchDto dto = new PersonPatchDto("F", null,null, null, null, null);
        ObjectNode json = objectMapper.createObjectNode();
        json.put("firstName", "F");

        Person person = Person.builder()
                .mission(Mission.builder().id(99L).build())
                .build();

        mapper.patchFromDtoToPerson(dto, json, person);
        assertThat(person.getMission()).isNotNull();
        assertThat(person.getMission().getId()).isEqualTo(99L);
    }

    @Test
    void toReadDto_nullPerson_returnsNull() {
        assertThat(mapper.toReadDto(null)).isNull();
    }

    @Test
    void toPerson_nullCreateDto_returnsNull() {
        assertThat(mapper.toPerson((PersonCreateDto) null)).isNull();
    }

    @Test
    void putFromDtoToPerson_nullDto_noChanges() {
        Person person = Person.builder().firstName("A").build();
        mapper.putFromDtoToPerson(null, person);
        assertThat(person.getFirstName()).isEqualTo("A");
    }

    @Test
    void toReadDto_setsMissionIdNullWhenMissionIdIsNull() {
        Person person = Person.builder()
                .id(3L)
                .firstName("Jane")
                .mission(Mission.builder().id(null).build())
                .build();
        PersonReadDto dto = mapper.toReadDto(person);
        assertThat(dto.missionId()).isNull();
    }

    @Test
    void personMissionId_returnsNull_whenPersonIsNull() throws Exception {
        Object impl = Mappers.getMapper(PersonMapper.class);
        Method m = impl.getClass().getDeclaredMethod("personMissionId", Person.class);
        m.setAccessible(true);
        assertThat(m.invoke(impl, (Person) null)).isNull();
    }
}
