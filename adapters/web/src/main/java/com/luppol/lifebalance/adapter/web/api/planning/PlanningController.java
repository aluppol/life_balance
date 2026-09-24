package com.luppol.lifebalance.adapter.web.api.planning;

import com.luppol.lifebalance.adapter.web.api.Created;
import com.luppol.lifebalance.application.planning.PlanActivity;
import com.luppol.lifebalance.application.planning.PlanningCommands;
import com.luppol.lifebalance.application.planning.PlanningQueries;
import com.luppol.lifebalance.application.planning.ReviseActivity;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.ActivityId;
import com.luppol.lifebalance.domain.planning.WeekStart;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Weekly planning", description = "Big rocks first: activities planned per week, role and quadrant")
public class PlanningController {
    public static final String WEEK_PATH = "/api/weeks/{weekStart}";
    public static final String ACTIVITY_PATH = "/api/activities";

    private final PlanningCommands commands;
    private final PlanningQueries queries;

    public PlanningController(PlanningCommands commands, PlanningQueries queries) {
        this.commands = commands;
        this.queries = queries;
    }

    @GetMapping(WEEK_PATH + "/activities")
    @Operation(summary = "List the activities of a week (weekStart is its Monday)")
    public List<ActivityResponse> listWeek(PersonId owner, @PathVariable WeekStart weekStart) {
        return queries.listWeek(owner, weekStart).stream().map(ActivityResponse::from).toList();
    }

    @GetMapping(WEEK_PATH + "/scorecard")
    @Operation(summary = "Planned and completed activities of a week, by quadrant and by role")
    public ScorecardResponse scorecard(PersonId owner, @PathVariable WeekStart weekStart) {
        return ScorecardResponse.from(queries.scorecard(owner, weekStart));
    }

    @PostMapping(WEEK_PATH + "/activities")
    @Operation(summary = "Plan an activity in a week")
    public ResponseEntity<ActivityResponse> plan(PersonId owner, @PathVariable WeekStart weekStart,
                                                 @Valid @RequestBody ActivityRequest request) {
        ActivityId id = ActivityId.random();
        commands.plan(new PlanActivity(id, owner, weekStart, request.toDetails()));
        return Created.at(ACTIVITY_PATH + "/" + id, find(owner, id.value()));
    }

    @GetMapping(ACTIVITY_PATH + "/{id}")
    @Operation(summary = "Read one activity")
    public ActivityResponse find(PersonId owner, @PathVariable UUID id) {
        return ActivityResponse.from(queries.find(owner, new ActivityId(id)));
    }

    @PutMapping(ACTIVITY_PATH + "/{id}")
    @Operation(summary = "Revise an activity inside its week")
    public ActivityResponse revise(PersonId owner, @PathVariable UUID id, @Valid @RequestBody ActivityRequest request) {
        commands.revise(new ReviseActivity(new ActivityId(id), owner, request.toDetails()));
        return find(owner, id);
    }

    @PutMapping(ACTIVITY_PATH + "/{id}/completion")
    @Operation(summary = "Mark an activity done")
    public ActivityResponse complete(PersonId owner, @PathVariable UUID id) {
        commands.complete(owner, new ActivityId(id));
        return find(owner, id);
    }

    @DeleteMapping(ACTIVITY_PATH + "/{id}/completion")
    @Operation(summary = "Mark an activity not done")
    public ActivityResponse reopen(PersonId owner, @PathVariable UUID id) {
        commands.reopen(owner, new ActivityId(id));
        return find(owner, id);
    }

    @DeleteMapping(ACTIVITY_PATH + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove an activity")
    public void remove(PersonId owner, @PathVariable UUID id) {
        commands.remove(owner, new ActivityId(id));
    }
}
