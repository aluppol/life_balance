package com.luppol.life_balance.mappers;

import com.luppol.life_balance.dto.PersonDto;
import com.luppol.life_balance.models.Person;
import org.mapstruct.*;

@Mapper(componentModel="spring")
public interface PersonMapper {
    @Mapping(target = "missionId", source = "mission.id")
    PersonDto toDto(Person person);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mission", ignore = true)
    Person toEntity(PersonDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mission", ignore = true)
    void merge(PersonDto source, @MappingTarget Person target);
}
