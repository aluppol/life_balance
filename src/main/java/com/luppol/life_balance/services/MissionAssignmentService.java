package com.luppol.life_balance.services;

import com.luppol.life_balance.dto.MissionCreateDto;
import com.luppol.life_balance.dto.MissionReadDto;
import com.luppol.life_balance.exceptions.PersonAlreadyHasMissionException;
import com.luppol.life_balance.mappers.MissionMapper;
import com.luppol.life_balance.models.Mission;
import com.luppol.life_balance.models.Person;
import com.luppol.life_balance.repositories.MissionRepository;
import com.luppol.life_balance.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MissionAssignmentService {
    private final MissionRepository missionRepository;
    private final PersonRepository personRepository;
    private final MissionMapper missionMapper;

    @Transactional
    public MissionReadDto createAndAssignToPerson (Long personId, MissionCreateDto missionDto) {
        Person person = personRepository.findRequired(personId);
        validatPersonHasNoMission(person);
        Mission mission = missionRepository.save(missionMapper.toMission(missionDto));
        assignMissionToPerson(person, mission);
        personRepository.save(person);
        return missionMapper.toReadDto(mission);
    }

    private void validatPersonHasNoMission(Person person) {
        if (person.getMission() != null) {
            throw new PersonAlreadyHasMissionException(person.getId());
        }
    }

    private void assignMissionToPerson(Person person, Mission mission) {
        person.setMission(mission);
    }
}
