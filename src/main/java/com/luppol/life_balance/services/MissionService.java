package com.luppol.life_balance.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.luppol.life_balance.dto.MissionCreateDto;
import com.luppol.life_balance.dto.MissionPatchDto;
import com.luppol.life_balance.dto.MissionPutDto;
import com.luppol.life_balance.dto.MissionReadDto;
import com.luppol.life_balance.mappers.MissionMapper;
import com.luppol.life_balance.models.Mission;
import com.luppol.life_balance.repositories.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService implements CrudService<Long, MissionCreateDto, MissionReadDto, MissionPutDto, MissionPatchDto> {
    private final MissionRepository missionRepo;
    private final MissionMapper missionMapper;

    @Override
    @Transactional
    public MissionReadDto create(MissionCreateDto dto) {
        Mission mission = missionRepo.save(missionMapper.toMission(dto));
        return missionMapper.toReadDto(mission);
    }

    @Override
    @Transactional(readOnly = true)
    public MissionReadDto getById(Long id) {
        return missionMapper.toReadDto(missionRepo.findRequired(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MissionReadDto> getAll() {
        return missionRepo.findAll().stream().map(missionMapper::toReadDto).toList();
    }

    @Override
    @Transactional
    public MissionReadDto put(Long id, MissionPutDto dto) {
        Mission mission = missionRepo.findRequired(id);
        missionMapper.putFromDtoToMission(dto, mission);
        return missionMapper.toReadDto(missionRepo.save(mission));
    }

    @Override
    @Transactional
    public MissionReadDto patch(Long id, MissionPatchDto dto, JsonNode jsonBody) {
        Mission mission = missionRepo.findRequired(id);
        missionMapper.patchFromDtoToMission(dto, jsonBody, mission);
        return missionMapper.toReadDto(missionRepo.save(mission));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        missionRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return missionRepo.count();
    }
}
