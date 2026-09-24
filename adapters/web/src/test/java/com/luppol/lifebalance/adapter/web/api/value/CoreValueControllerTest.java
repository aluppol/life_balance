package com.luppol.lifebalance.adapter.web.api.value;

import com.luppol.lifebalance.adapter.web.WebTest;
import com.luppol.lifebalance.application.value.AddCoreValue;
import com.luppol.lifebalance.application.value.ReorderCoreValues;
import com.luppol.lifebalance.application.value.ReviseCoreValue;
import com.luppol.lifebalance.domain.ConflictException;
import com.luppol.lifebalance.domain.NotFoundException;
import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CoreValueControllerTest extends WebTest {
    private final CoreValue integrity = new CoreValue(CoreValueId.random(), OWNER, "Integrity", "Keep promises", 0);

    @Test
    void listAll_returnsTheRankedValues() throws Exception {
        when(coreValueQueries.listAll(OWNER)).thenReturn(List.of(integrity));

        mvc.perform(get("/api/values").with(member()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(integrity.id().toString()))
                .andExpect(jsonPath("$[0].name").value("Integrity"))
                .andExpect(jsonPath("$[0].description").value("Keep promises"))
                .andExpect(jsonPath("$[0].position").value(0));
    }

    @Test
    void add_createsTheValueAndPointsAtIt() throws Exception {
        when(coreValueQueries.find(any(), any())).thenReturn(integrity);

        mvc.perform(post("/api/values").with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Integrity\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/values/")))
                .andExpect(jsonPath("$.name").value("Integrity"));

        ArgumentCaptor<AddCoreValue> added = ArgumentCaptor.forClass(AddCoreValue.class);
        verify(coreValueCommands).add(added.capture());
        assertThat(added.getValue().owner()).isEqualTo(OWNER);
        assertThat(added.getValue().name()).isEqualTo("Integrity");
        assertThat(added.getValue().description()).isEmpty();
    }

    @Test
    void add_rejectsANameLongerThan100Characters() throws Exception {
        mvc.perform(post("/api/values").with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"%s\"}".formatted("n".repeat(101))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void add_reportsADuplicateAsConflict() throws Exception {
        doThrow(new ConflictException("A core value named 'Integrity' already exists")).when(coreValueCommands).add(any());

        mvc.perform(post("/api/values").with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Integrity\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("A core value named 'Integrity' already exists"));
    }

    @Test
    void revise_changesTheValue() throws Exception {
        UUID id = integrity.id().value();
        when(coreValueQueries.find(OWNER, integrity.id())).thenReturn(integrity);

        mvc.perform(put("/api/values/{id}", id).with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Integrity\",\"description\":\"Keep promises\"}"))
                .andExpect(status().isOk());

        verify(coreValueCommands).revise(new ReviseCoreValue(integrity.id(), OWNER, "Integrity", "Keep promises"));
    }

    @Test
    void reorder_ranksTheValues() throws Exception {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();

        mvc.perform(put("/api/values/order").with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ids\":[\"%s\",\"%s\"]}".formatted(first, second)))
                .andExpect(status().isNoContent());

        verify(coreValueCommands).reorder(new ReorderCoreValues(OWNER, List.of(new CoreValueId(first), new CoreValueId(second))));
    }

    @Test
    void reorder_reportsAnIncompleteOrderAsUnprocessable() throws Exception {
        doThrow(new RuleViolationException("The new order must list every item exactly once"))
                .when(coreValueCommands).reorder(any());

        mvc.perform(put("/api/values/order").with(member()).contentType(MediaType.APPLICATION_JSON).content("{\"ids\":[]}"))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void remove_deletesTheValue() throws Exception {
        mvc.perform(delete("/api/values/{id}", integrity.id().value()).with(member()))
                .andExpect(status().isNoContent());

        verify(coreValueCommands).remove(OWNER, integrity.id());
    }

    @Test
    void find_reportsAnUnknownValue() throws Exception {
        CoreValueId unknown = CoreValueId.random();
        when(coreValueQueries.find(OWNER, unknown)).thenThrow(new NotFoundException("Core value", unknown));

        mvc.perform(get("/api/values/{id}", unknown.value()).with(member())).andExpect(status().isNotFound());
    }

    @Test
    void find_rejectsAMalformedId() throws Exception {
        mvc.perform(get("/api/values/not-a-uuid").with(member())).andExpect(status().isBadRequest());
    }
}
