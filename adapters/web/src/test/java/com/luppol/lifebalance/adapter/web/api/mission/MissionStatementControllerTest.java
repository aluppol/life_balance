package com.luppol.lifebalance.adapter.web.api.mission;

import com.luppol.lifebalance.adapter.web.WebTest;
import com.luppol.lifebalance.domain.NotFoundException;
import com.luppol.lifebalance.domain.mission.MissionStatement;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MissionStatementControllerTest extends WebTest {
    @Test
    void find_returnsTheStatement() throws Exception {
        when(missionStatementQueries.find(OWNER)).thenReturn(new MissionStatement(OWNER, "Live by principles"));

        mvc.perform(get("/api/mission").with(member()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Live by principles"));
    }

    @Test
    void find_reportsAMissingStatementAsProblem() throws Exception {
        when(missionStatementQueries.find(OWNER)).thenThrow(new NotFoundException("Mission statement of", OWNER));

        mvc.perform(get("/api/mission").with(member()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Mission statement of owner-subject not found"));
    }

    @Test
    void define_storesTheStatementOfTheSignedInPerson() throws Exception {
        MissionStatement statement = new MissionStatement(OWNER, "Serve others");
        when(missionStatementQueries.find(OWNER)).thenReturn(statement);

        mvc.perform(put("/api/mission").with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Serve others\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Serve others"));

        verify(missionStatementCommands).define(statement);
    }

    @Test
    void define_rejectsABlankStatement() throws Exception {
        mvc.perform(put("/api/mission").with(member()).contentType(MediaType.APPLICATION_JSON).content("{\"text\":\" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.text").exists());
    }
}
