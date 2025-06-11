package com.luppol.life_balance.mappers;

import com.fasterxml.jackson.databind.JsonNode;

import com.luppol.life_balance.dto.MissionCreateDto;
import com.luppol.life_balance.dto.MissionPatchDto;
import com.luppol.life_balance.dto.MissionPutDto;
import com.luppol.life_balance.dto.MissionReadDto;
import com.luppol.life_balance.models.Mission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel="spring")
public interface MissionMapper extends BaseMapper {
    MissionReadDto toReadDto(Mission mission);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    Mission toMission(MissionCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    void putFromDtoToMission(MissionPutDto dto, @MappingTarget Mission mission);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    default void patchFromDtoToMission(MissionPatchDto dto, JsonNode jsonBody, @MappingTarget Mission mission) {
        patch(jsonBody, "text", dto.text(), mission::setText);
    }
}
