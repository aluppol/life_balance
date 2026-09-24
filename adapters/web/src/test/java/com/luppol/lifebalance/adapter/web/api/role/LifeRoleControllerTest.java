package com.luppol.lifebalance.adapter.web.api.role;

import com.luppol.lifebalance.adapter.web.WebTest;
import com.luppol.lifebalance.application.role.AddLifeRole;
import com.luppol.lifebalance.application.role.ReorderLifeRoles;
import com.luppol.lifebalance.application.role.ReviseLifeRole;
import com.luppol.lifebalance.domain.ConflictException;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LifeRoleControllerTest extends WebTest {
    private final LifeRole saw = LifeRole.sharpenTheSaw(OWNER);
    private final LifeRole parent = LifeRole.personal(LifeRoleId.random(), OWNER, "Parent", "", 1);

    @Test
    void listAll_showsKinds() throws Exception {
        when(lifeRoleQueries.listAll(OWNER)).thenReturn(List.of(saw, parent));

        mvc.perform(get("/api/roles").with(member()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].kind").value("SHARPEN_THE_SAW"))
                .andExpect(jsonPath("$[1].name").value("Parent"))
                .andExpect(jsonPath("$[1].kind").value("PERSONAL"))
                .andExpect(jsonPath("$[1].position").value(1));
    }

    @Test
    void add_createsAPersonalRole() throws Exception {
        when(lifeRoleQueries.find(any(), any())).thenReturn(parent);

        mvc.perform(post("/api/roles").with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Parent\",\"description\":\"Raise kind kids\"}"))
                .andExpect(status().isCreated());

        ArgumentCaptor<AddLifeRole> added = ArgumentCaptor.forClass(AddLifeRole.class);
        verify(lifeRoleCommands).add(added.capture());
        assertThat(added.getValue().description()).isEqualTo("Raise kind kids");
    }

    @Test
    void revise_andReorder_passTheRequest() throws Exception {
        when(lifeRoleQueries.find(OWNER, parent.id())).thenReturn(parent);

        mvc.perform(put("/api/roles/{id}", parent.id().value()).with(member()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Father\"}")).andExpect(status().isOk());
        mvc.perform(put("/api/roles/order").with(member()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[\"%s\",\"%s\"]}".formatted(parent.id(), saw.id()))).andExpect(status().isNoContent());

        verify(lifeRoleCommands).revise(new ReviseLifeRole(parent.id(), OWNER, "Father", ""));
        verify(lifeRoleCommands).reorder(new ReorderLifeRoles(OWNER, List.of(parent.id(), saw.id())));
    }

    @Test
    void remove_reportsTheBuiltInRoleAsConflict() throws Exception {
        doThrow(new ConflictException("The Sharpen the Saw role cannot be removed"))
                .when(lifeRoleCommands).remove(OWNER, saw.id());

        mvc.perform(delete("/api/roles/{id}", saw.id().value()).with(member()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("The Sharpen the Saw role cannot be removed"));
    }

    @Test
    void remove_deletesAPersonalRole() throws Exception {
        mvc.perform(delete("/api/roles/{id}", parent.id().value()).with(member())).andExpect(status().isNoContent());

        verify(lifeRoleCommands).remove(OWNER, parent.id());
    }

    @Test
    void add_requiresAName() throws Exception {
        mvc.perform(post("/api/roles").with(member()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }
}
