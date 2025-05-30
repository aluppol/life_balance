package com.luppol.life_balance.mappers;

import com.luppol.life_balance.dto.PersonCreateDto;
import com.luppol.life_balance.dto.PersonPatchDto;
import com.luppol.life_balance.dto.PersonPutDto;
import com.luppol.life_balance.dto.PersonReadDto;
import com.luppol.life_balance.models.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel="spring")
public interface PersonMapper {
    @Mapping(target = "missionId", source = "mission.id")
    PersonReadDto toReadDto(Person person);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mission", ignore = true)
    Person toPerson(PersonCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mission", ignore = true)
    void putFromDtoToPerson(PersonPutDto dto, @MappingTarget Person person);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mission", ignore = true)
    default void patchFromDtoToPerson(PersonPatchDto dto, @MappingTarget Person person) {
        dto.firstName().ifPresent(person::setFirstName);
        dto.lastName().ifPresent(person::setLastName);
        dto.middleName().ifPresent(person::setMiddleName);
        dto.address().ifPresent(person::setAddress);
        dto.phoneNumber().ifPresent(person::setPhoneNumber);
    }
}
