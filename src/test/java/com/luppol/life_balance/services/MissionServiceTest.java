package com.luppol.life_balance.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luppol.life_balance.dto.MissionCreateDto;
import com.luppol.life_balance.dto.MissionPatchDto;
import com.luppol.life_balance.dto.MissionPutDto;
import com.luppol.life_balance.dto.MissionReadDto;
import com.luppol.life_balance.exceptions.NotFoundException;
import com.luppol.life_balance.mappers.MissionMapper;
import com.luppol.life_balance.models.Mission;
import com.luppol.life_balance.repositories.MissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MissionServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    MissionRepository repo;

    @Mock
    MissionMapper mapper;

    @InjectMocks
    MissionService service;

    @Test
    void create_succeeds() {
        MissionCreateDto createDto = new MissionCreateDto("A");
        MissionReadDto readDto = new MissionReadDto(1L, "A");
        Mission missionToSave = Mission.builder().text("A").build();
        Mission missionSaved = Mission.builder().id(1L).text("A").build();

        when(mapper.toMission(createDto)).thenReturn(missionToSave);
        when(mapper.toReadDto(missionSaved)).thenReturn(readDto);
        when(repo.save(missionToSave)).thenReturn(missionSaved);

        assertEquals(readDto, service.create(createDto));
        verify(mapper).toMission(createDto);
        verify(repo).save(missionToSave);
        verify(mapper).toReadDto(missionSaved);
    }

    @Test
    void createEntity_persists_and_returns_entity() {
        MissionCreateDto dto   = new MissionCreateDto("A");
        Mission toSave= Mission.builder().text("A").build();
        Mission saved = Mission.builder().id(1L).text("A").build();

        when(mapper.toMission(dto)).thenReturn(toSave);
        when(repo.save(toSave)).thenReturn(saved);

        assertEquals(saved, service.createEntity(dto));
        verify(mapper).toMission(dto);
        verify(repo).save(toSave);
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
        Mission mission = Mission.builder().id(ID).text("T").build();
        MissionReadDto readDto = new MissionReadDto(1L, "T");

        when(repo.findById(ID)).thenReturn(Optional.of(mission));
        when(mapper.toReadDto(mission)).thenReturn(readDto);

        assertEquals(readDto, service.getById(ID));
    }

    @Test
    void getAll() {
        when(repo.findAll()).thenReturn(List.of(new Mission(), new Mission()));
        when(mapper.toReadDto(new Mission())).thenReturn(new MissionReadDto(1L, "M"));
        assertEquals(2, service.getAll().size());
    }

    @Test
    void put_notFound() {
        final long ID = 1L;
        when(repo.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.put(ID, new MissionPutDto("M")));
    }

    @Test
    void put_succeeds() {
        final long ID = 1L;
        Mission missionOld = Mission.builder().id(ID).build();
        Mission missionUpdated = Mission.builder().id(ID).text("X").build();
        MissionPutDto putDto = new MissionPutDto("X");
        MissionReadDto readDto = new MissionReadDto(1L, "X");

        when(repo.findById(ID)).thenReturn(Optional.of(missionOld));
        when(repo.save(missionOld)).thenReturn(missionUpdated);

        when(mapper.toReadDto(missionUpdated)).thenReturn(readDto);
        doAnswer(putMissionFromDtoInvocation -> {
            MissionPutDto dto = putMissionFromDtoInvocation.getArgument(0);
            Mission mission = putMissionFromDtoInvocation.getArgument(1);

            mission.setText(dto.text());
            return null;
        }).when(mapper).putFromDtoToMission(eq(putDto), eq(missionOld));

        assertEquals(readDto, service.put(ID, putDto));

        verify(repo).save(missionUpdated);
        verify(mapper).putFromDtoToMission(putDto, missionOld);
    }

    @Test
    void patch_notFound() {
        final long ID = 1L;

        when(repo.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.patch(
                ID,
                new MissionPatchDto("M"),
                objectMapper.createObjectNode()
        ));
    }

    @Test
    void patch_succeeds() {
        final long ID = 1L;
        final String TEXT = "X";

        JsonNode jsonBody = objectMapper.createObjectNode()
                .put("text", TEXT);
        Mission missionOld = Mission.builder()
                .id(ID)
                .text("Old X")
                .build();
        Mission missionPatched = Mission.builder()
                .id(ID)
                .text(TEXT)
                .build();

        MissionPatchDto patchDto = new MissionPatchDto(TEXT);
        MissionReadDto readDto = new MissionReadDto(ID, TEXT);

        when(repo.findById(ID)).thenReturn(Optional.of(missionOld));
        when(repo.save(missionPatched)).thenReturn(missionPatched);

        when(mapper.toReadDto(missionPatched)).thenReturn(readDto);
        doAnswer(patchMissionFromDtoInvocation -> {
            MissionPatchDto dto = patchMissionFromDtoInvocation.getArgument(0);
            JsonNode json = patchMissionFromDtoInvocation.getArgument(1);
            Mission mission = patchMissionFromDtoInvocation.getArgument(2);

            if (json.has("text")) {
                mission.setText(dto.text());
            }

            return null;
        }).when(mapper).patchFromDtoToMission(eq(patchDto), eq(jsonBody), eq(missionOld));

        assertEquals(readDto, service.patch(ID, patchDto, jsonBody));

        verify(mapper).patchFromDtoToMission(patchDto, jsonBody, missionOld);
        verify(mapper).toReadDto(missionPatched);
        verify(repo).findById(ID);
        verify(repo).save(missionOld);
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
