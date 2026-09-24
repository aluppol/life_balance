package com.luppol.lifebalance.adapter.web.api.role;

import com.luppol.lifebalance.adapter.web.api.Created;
import com.luppol.lifebalance.adapter.web.api.OrderRequest;
import com.luppol.lifebalance.application.role.LifeRoleCommands;
import com.luppol.lifebalance.application.role.LifeRoleQueries;
import com.luppol.lifebalance.application.role.ReorderLifeRoles;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(LifeRoleController.BASE_PATH)
@Tag(name = "Life roles", description = "The roles a person plays; Sharpen the Saw is built in")
public class LifeRoleController {
    public static final String BASE_PATH = "/api/roles";

    private final LifeRoleCommands commands;
    private final LifeRoleQueries queries;

    public LifeRoleController(LifeRoleCommands commands, LifeRoleQueries queries) {
        this.commands = commands;
        this.queries = queries;
    }

    @GetMapping
    @Operation(summary = "List life roles in their chosen order")
    public List<LifeRoleResponse> listAll(PersonId owner) {
        return queries.listAll(owner).stream().map(LifeRoleResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Read one life role")
    public LifeRoleResponse find(PersonId owner, @PathVariable UUID id) {
        return LifeRoleResponse.from(queries.find(owner, new LifeRoleId(id)));
    }

    @PostMapping
    @Operation(summary = "Add a life role at the end of the list")
    public ResponseEntity<LifeRoleResponse> add(PersonId owner, @Valid @RequestBody LifeRoleRequest request) {
        LifeRoleId id = LifeRoleId.random();
        commands.add(request.toAddition(id, owner));
        return Created.at(BASE_PATH + "/" + id, find(owner, id.value()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Rename or redescribe a life role")
    public LifeRoleResponse revise(PersonId owner, @PathVariable UUID id, @Valid @RequestBody LifeRoleRequest request) {
        commands.revise(request.toRevision(new LifeRoleId(id), owner));
        return find(owner, id);
    }

    @PutMapping("/order")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Order all life roles; the list must hold every id exactly once")
    public void reorder(PersonId owner, @Valid @RequestBody OrderRequest request) {
        commands.reorder(new ReorderLifeRoles(owner, request.idsAs(LifeRoleId::new)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a personal role that has no goals or activities")
    public void remove(PersonId owner, @PathVariable UUID id) {
        commands.remove(owner, new LifeRoleId(id));
    }
}
