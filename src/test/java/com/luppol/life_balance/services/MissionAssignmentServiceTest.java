package com.luppol.life_balance.services;

import com.luppol.life_balance.dto.MissionCreateDto;
import com.luppol.life_balance.dto.MissionReadDto;
import com.luppol.life_balance.exceptions.PersonAlreadyHasMissionException;
import com.luppol.life_balance.mappers.MissionMapper;
import com.luppol.life_balance.models.Mission;
import com.luppol.life_balance.models.Person;
import com.luppol.life_balance.repositories.MissionRepository;
import com.luppol.life_balance.repositories.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MissionAssignmentServiceTest {
    @Mock
    PersonRepository personRepository;

    @Mock
    MissionRepository missionRepository;

    @Mock
    MissionMapper missionMapper;

    @InjectMocks
    MissionAssignmentService missionAssignmentService;

    @Test
    void createAndAssignMission_succeeds() {
        Long personId = 1L;
        Long missionId = 10L;
        String missionText = "A";

        MissionCreateDto dto = new MissionCreateDto(missionText);
        Mission missionToCreate = Mission.builder().text(missionText).build();
        Mission missionCreated = Mission.builder().id(missionId).text(missionText).build();
        MissionReadDto readDto = new MissionReadDto(missionId, missionText);

        Person initialPerson = Person.builder().id(personId).build();
        Person personWithMissionAssigned = Person.builder().id(personId).mission(missionCreated).build();

        when(personRepository.findRequired(personId)).thenReturn(initialPerson);
        when(missionMapper.toMission(dto)).thenReturn(missionToCreate);
        when(missionRepository.save(missionToCreate)).thenReturn(missionCreated);
        when(personRepository.save(personWithMissionAssigned)).thenReturn(personWithMissionAssigned);
        when(missionMapper.toReadDto(missionCreated)).thenReturn(readDto);

        MissionReadDto result = missionAssignmentService.createAndAssignToPerson(personId, dto);

        assertEquals(readDto, result);

        verify(personRepository).findRequired(personId);
        verify(missionMapper).toMission(dto);
        verify(missionRepository).save(missionToCreate);
        verify(personRepository).save(personWithMissionAssigned);
        verify(missionMapper).toReadDto(missionCreated);
    }

    @Test
    void createAndAssignMission_personHasMission_throws() {
        Long personId = 1L;
        MissionCreateDto dto = new MissionCreateDto("A");
        Mission existingMission = Mission.builder().id(9L).text("B").build();
        Person person = Person.builder().id(personId).mission(existingMission).build();

        when(personRepository.findRequired(personId)).thenReturn(person);

        assertThrows(PersonAlreadyHasMissionException.class,
                () -> missionAssignmentService.createAndAssignToPerson(personId, dto));

        verify(missionRepository, never()).save(any());
        verify(personRepository, never()).save(any());
    }
}
