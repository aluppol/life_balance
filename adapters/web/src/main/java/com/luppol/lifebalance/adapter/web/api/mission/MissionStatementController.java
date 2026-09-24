package com.luppol.lifebalance.adapter.web.api.mission;

import com.luppol.lifebalance.application.mission.MissionStatementCommands;
import com.luppol.lifebalance.application.mission.MissionStatementQueries;
import com.luppol.lifebalance.domain.person.PersonId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mission")
@Tag(name = "Mission", description = "The personal mission statement")
public class MissionStatementController {
    private final MissionStatementCommands commands;
    private final MissionStatementQueries queries;

    public MissionStatementController(MissionStatementCommands commands, MissionStatementQueries queries) {
        this.commands = commands;
        this.queries = queries;
    }

    @GetMapping
    @Operation(summary = "Read the mission statement (404 until one is written)")
    public MissionStatementResponse find(PersonId owner) {
        return MissionStatementResponse.from(queries.find(owner));
    }

    @PutMapping
    @Operation(summary = "Write or rewrite the mission statement")
    public MissionStatementResponse define(PersonId owner, @Valid @RequestBody MissionStatementRequest request) {
        commands.define(request.toStatement(owner));
        return find(owner);
    }
}
