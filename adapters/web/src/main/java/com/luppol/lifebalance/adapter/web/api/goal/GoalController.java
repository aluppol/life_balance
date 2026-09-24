package com.luppol.lifebalance.adapter.web.api.goal;

import com.luppol.lifebalance.adapter.web.api.Created;
import com.luppol.lifebalance.application.goal.GoalCommands;
import com.luppol.lifebalance.application.goal.GoalQueries;
import com.luppol.lifebalance.application.goal.ReviseGoal;
import com.luppol.lifebalance.application.goal.SetGoal;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.person.PersonId;
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
@RequestMapping(GoalController.BASE_PATH)
@Tag(name = "Goals", description = "Goals rooted in a life role and in core values")
public class GoalController {
    public static final String BASE_PATH = "/api/goals";

    private final GoalCommands commands;
    private final GoalQueries queries;

    public GoalController(GoalCommands commands, GoalQueries queries) {
        this.commands = commands;
        this.queries = queries;
    }

    @GetMapping
    @Operation(summary = "List goals in the order they were set")
    public List<GoalResponse> listAll(PersonId owner) {
        return queries.listAll(owner).stream().map(GoalResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Read one goal")
    public GoalResponse find(PersonId owner, @PathVariable UUID id) {
        return GoalResponse.from(queries.find(owner, new GoalId(id)));
    }

    @PostMapping
    @Operation(summary = "Set a new, active goal")
    public ResponseEntity<GoalResponse> set(PersonId owner, @Valid @RequestBody GoalRequest request) {
        GoalId id = GoalId.random();
        commands.set(new SetGoal(id, owner, request.toDetails()));
        return Created.at(BASE_PATH + "/" + id, find(owner, id.value()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Revise what a goal is about")
    public GoalResponse revise(PersonId owner, @PathVariable UUID id, @Valid @RequestBody GoalRequest request) {
        commands.revise(new ReviseGoal(new GoalId(id), owner, request.toDetails()));
        return find(owner, id);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Achieve, drop or reopen a goal")
    public GoalResponse changeStatus(PersonId owner, @PathVariable UUID id,
                                     @Valid @RequestBody GoalStatusRequest request) {
        GoalId goalId = new GoalId(id);
        switch (request.status()) {
            case ACHIEVED -> commands.achieve(owner, goalId);
            case DROPPED -> commands.drop(owner, goalId);
            case ACTIVE -> commands.reopen(owner, goalId);
        }
        return find(owner, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a goal; its activities stay, unlinked")
    public void remove(PersonId owner, @PathVariable UUID id) {
        commands.remove(owner, new GoalId(id));
    }
}
