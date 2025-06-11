package com.luppol.life_balance.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.luppol.life_balance.dto.MissionCreateDto;
import com.luppol.life_balance.dto.MissionPatchDto;
import com.luppol.life_balance.dto.MissionPutDto;
import com.luppol.life_balance.dto.MissionReadDto;
import com.luppol.life_balance.models.Mission;
import com.luppol.life_balance.models.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class MissionMapperTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    MissionMapper mapper = Mappers.getMapper(MissionMapper.class);

    @Test
    void toReadDto_mapsAllFields() {
        Mission mission = Mission.builder()
                .id(42L)
                .text("To be Happy!")
                .build();
        MissionReadDto dto = mapper.toReadDto(mission);
        assertThat(dto.id()).isEqualTo(42L);
        assertThat(dto.text()).isEqualTo("To be Happy!");
    }

    @Test
    void toMission_mapsCreateDto() {
        MissionCreateDto dto = new MissionCreateDto("To be Happy!");
        Mission mission = mapper.toMission(dto);
        assertThat(mission.getText()).isEqualTo("To be Happy!");
    }

    @Test
    void putFromDtoToMission_overwritesAllFields() {
        MissionPutDto putDto = new MissionPutDto("To be Happy!");
        Mission mission = Mission.builder().text("To be Very Happy!").build();
        mapper.putFromDtoToMission(putDto, mission);
        assertThat(mission.getText()).isEqualTo("To be Happy!");
    }

    @Test
    void patchFromDtoToMission_onlyPatchedFieldsAreChanged() {
        MissionPatchDto patchDto = new MissionPatchDto("Patched Mission");
        ObjectNode json = objectMapper.createObjectNode();
        json.put("text", "Patched Mission");
        Mission mission = Mission.builder().text("Original Mission").build();
        mapper.patchFromDtoToMission(patchDto, json, mission);
        assertThat(mission.getText()).isEqualTo("Patched Mission");
        // add here if more fields to play with exist
    }

    @Test
    void patchFromDtoToMission_clearsFieldWhenExplicitlyNull() {
        MissionPatchDto dto = new MissionPatchDto(null);
        ObjectNode json = objectMapper.createObjectNode();
        json.putNull("text");

        Mission mission = new Mission();
        mission.setText("ToBeCleared");

        mapper.patchFromDtoToMission(dto, json, mission);

        assertNull(mission.getText());
    }

    @Test
    void patchFromDtoToMission_doesNothingIfFieldNotPresentInJson() {
        MissionPatchDto dto = new MissionPatchDto("ShouldNotApply");
        ObjectNode json = objectMapper.createObjectNode();

        Mission mission = new Mission();
        mission.setText("Original");

        mapper.patchFromDtoToMission(dto, json, mission);

        assertEquals("Original", mission.getText());
    }

    @Test
    void patchFromDtoToMission_doesNothingWhenNoFieldsPresent() {
        MissionPatchDto dto = new MissionPatchDto("newMission");
        ObjectNode json = objectMapper.createObjectNode();

        Mission mission = new Mission();
        mission.setText("OldMission");

        mapper.patchFromDtoToMission(dto, json, mission);

        assertEquals("OldMission", mission.getText()); // unchanged
    }

    @Test
    void toMission_ignoresPersonOnCreate() {
        MissionCreateDto dto = new MissionCreateDto("Mission");
        Mission mission = mapper.toMission(dto);
        assertThat(mission.getPerson()).isNull();
    }

    @Test
    void putFromDtoToMission_doesNotOverwritePerson() {
        MissionPutDto dto = new MissionPutDto("Mission");
        Mission original = Mission.builder()
                .person(Person.builder().id(42L).build())
                .build();
        mapper.putFromDtoToMission(dto, original);
        assertThat(original.getPerson()).isNotNull();
        assertThat(original.getPerson().getId()).isEqualTo(42L);
    }

    @Test
    void patchFromDtoToMission_doesNotAffectPerson() {
        MissionPatchDto dto = new MissionPatchDto("M");
        ObjectNode json = objectMapper.createObjectNode();
        json.put("text", "M");

        Mission mission = Mission.builder()
                .person(Person.builder().id(99L).build())
                .build();

        mapper.patchFromDtoToMission(dto, json, mission);
        assertThat(mission.getPerson()).isNotNull();
        assertThat(mission.getPerson().getId()).isEqualTo(99L);
    }

    @Test
    void toReadDto_nullMission_returnsNull() {
        assertThat(mapper.toReadDto(null)).isNull();
    }

    @Test
    void toMission_nullCreateDto_returnsNull() {
        assertThat(mapper.toMission((MissionCreateDto) null)).isNull();
    }

    @Test
    void putFromDtoToMission_nullDto_noChanges() {
        Mission mission = Mission.builder().text("A").build();
        mapper.putFromDtoToMission(null, mission);
        assertThat(mission.getText()).isEqualTo("A");
    }
}
