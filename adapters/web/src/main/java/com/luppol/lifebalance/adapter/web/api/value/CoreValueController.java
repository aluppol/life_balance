package com.luppol.lifebalance.adapter.web.api.value;

import com.luppol.lifebalance.adapter.web.api.Created;
import com.luppol.lifebalance.adapter.web.api.OrderRequest;
import com.luppol.lifebalance.application.value.CoreValueCommands;
import com.luppol.lifebalance.application.value.CoreValueQueries;
import com.luppol.lifebalance.application.value.ReorderCoreValues;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.value.CoreValueId;
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
@RequestMapping(CoreValueController.BASE_PATH)
@Tag(name = "Core values", description = "The principles a person ranks and lives by")
public class CoreValueController {
    public static final String BASE_PATH = "/api/values";

    private final CoreValueCommands commands;
    private final CoreValueQueries queries;

    public CoreValueController(CoreValueCommands commands, CoreValueQueries queries) {
        this.commands = commands;
        this.queries = queries;
    }

    @GetMapping
    @Operation(summary = "List core values, highest ranked first")
    public List<CoreValueResponse> listAll(PersonId owner) {
        return queries.listAll(owner).stream().map(CoreValueResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Read one core value")
    public CoreValueResponse find(PersonId owner, @PathVariable UUID id) {
        return CoreValueResponse.from(queries.find(owner, new CoreValueId(id)));
    }

    @PostMapping
    @Operation(summary = "Add a core value at the bottom of the ranking")
    public ResponseEntity<CoreValueResponse> add(PersonId owner, @Valid @RequestBody CoreValueRequest request) {
        CoreValueId id = CoreValueId.random();
        commands.add(request.toAddition(id, owner));
        return Created.at(BASE_PATH + "/" + id, find(owner, id.value()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Rename or redescribe a core value")
    public CoreValueResponse revise(PersonId owner, @PathVariable UUID id, @Valid @RequestBody CoreValueRequest request) {
        commands.revise(request.toRevision(new CoreValueId(id), owner));
        return find(owner, id);
    }

    @PutMapping("/order")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Rank all core values; the list must hold every id exactly once")
    public void reorder(PersonId owner, @Valid @RequestBody OrderRequest request) {
        commands.reorder(new ReorderCoreValues(owner, request.idsAs(CoreValueId::new)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a core value; goals keep going without it")
    public void remove(PersonId owner, @PathVariable UUID id) {
        commands.remove(owner, new CoreValueId(id));
    }
}
