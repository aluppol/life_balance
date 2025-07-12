package com.luppol.life_balance.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.luppol.life_balance.dto.*;
import com.luppol.life_balance.exceptions.NotFoundException;
import com.luppol.life_balance.mappers.MissionMapper;
import com.luppol.life_balance.models.Mission;
import com.luppol.life_balance.repositories.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MissionService implements CrudService<Long, MissionCreateDto, MissionReadDto, MissionPutDto, MissionPatchDto> {
    private final MissionRepository missionRepo;
    private final MissionMapper missionMapper;

    @Override
    public MissionReadDto create(MissionCreateDto dto) {
        return missionMapper.toReadDto(createEntity(dto));
    }

    public Mission createEntity(MissionCreateDto dto) {
        Mission mission = missionMapper.toMission(dto);
        return missionRepo.save(mission);
    }

    @Override
    public MissionReadDto getById(Long id) {
        return missionMapper.toReadDto(getMissionById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MissionReadDto> getAll() {
        return missionRepo.findAll().stream().map(missionMapper::toReadDto).toList();
    }

    @Override
    public MissionReadDto put(Long id, MissionPutDto dto) {
        Mission mission = getMissionById(id);
        missionMapper.putFromDtoToMission(dto, mission);
        return missionMapper.toReadDto(missionRepo.save(mission));
    }

    @Override
    public MissionReadDto patch(Long id, MissionPatchDto dto, JsonNode jsonBody) {
        Mission mission = getMissionById(id);
        missionMapper.patchFromDtoToMission(dto, jsonBody, mission);
        return missionMapper.toReadDto(missionRepo.save(mission));
    }

    @Override
    public void deleteById(Long id) {
        missionRepo.deleteById(id);
    }

    @Override
    public long count() {
        return missionRepo.count();
    }

    @Transactional(readOnly = true)
    private Mission getMissionById(Long id) {
        return missionRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Mission with id %d not found!", id)));
    }
}
