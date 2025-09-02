package com.luppol.life_balance.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.luppol.life_balance.dto.PersonCreateDto;
import com.luppol.life_balance.dto.PersonPatchDto;
import com.luppol.life_balance.dto.PersonPutDto;
import com.luppol.life_balance.dto.PersonReadDto;
import com.luppol.life_balance.models.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel="spring")
public interface PersonMapper extends BaseMapper {
    @Mapping(target = "missionId", source = "mission.id")
    PersonReadDto toReadDto(Person person);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "mission", ignore = true)
    Person toPerson(PersonCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "mission", ignore = true)
    void putFromDtoToPerson(PersonPutDto dto, @MappingTarget Person person);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "mission", ignore = true)
    default void patchFromDtoToPerson(PersonPatchDto dto, JsonNode jsonBody,  @MappingTarget Person person) {
        patch(jsonBody, "firstName", dto.firstName(), person::setFirstName);
        patch(jsonBody, "lastName", dto.lastName(), person::setLastName);
        patch(jsonBody, "middleName", dto.middleName(), person::setMiddleName);
        patch(jsonBody, "phoneNumber", dto.phoneNumber(), person::setPhoneNumber);
        patch(jsonBody, "address", dto.address(), person::setAddress);
    }
}
