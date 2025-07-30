package com.luppol.life_balance.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luppol.life_balance.dto.PersonCreateDto;
import com.luppol.life_balance.dto.PersonPutDto;
import com.luppol.life_balance.dto.PersonPatchDto;
import com.luppol.life_balance.dto.PersonReadDto;
import com.luppol.life_balance.services.PersonService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(PersonController.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Person", description = "Person management API")
public class PersonController {
    public static final String BASE_PATH = "/api/persons";

    private final PersonService personService;
    private final ObjectMapper objectMapper;

    @Autowired
    private Validator validator;

    @Operation(
        summary = "Create a new Person",
        description = "Creates a new person record and returns the created person.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Person created",
                content = @Content(schema = @Schema(implementation = PersonReadDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Duplicate Person or phone number")
        }
    )
    @PostMapping
    public ResponseEntity<PersonReadDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Person creation payload",
                required = true,
                content = @Content(schema = @Schema(implementation = PersonCreateDto.class))
            )
            @Valid @RequestBody PersonCreateDto body
    ) {
        PersonReadDto saved = personService.create(body);
        return ResponseEntity
                .created(URI.create(BASE_PATH + "/" + saved.id()))
                .body(saved);
    }

    @Operation(
        summary = "Get all persons",
        description = "Returns a list of all persons."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of persons",
        content = @Content(schema = @Schema(implementation = PersonReadDto.class))
    )
    @GetMapping
    public ResponseEntity<List<PersonReadDto>> getAll() {
        return ResponseEntity.ok(personService.getAll());
    }

    @Operation(
        summary = "Get person by ID",
        description = "Returns the person with the specified ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Person found",
            content = @Content(schema = @Schema(implementation = PersonReadDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Person not found"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<PersonReadDto> getById(
            @Parameter(description = "ID of the person to retrieve", required = true)
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(personService.getById(id));
    }

    @Operation(
        summary = "Replace person",
        description = "Fully updates an existing person (PUT semantics)."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Person updated",
            content = @Content(schema = @Schema(implementation = PersonReadDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Person not found"
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<PersonReadDto> put(
            @Parameter(description = "ID of the person to update", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Person replacement payload",
                required = true,
                content = @Content(schema = @Schema(implementation = PersonPutDto.class))
            )
            @Valid @RequestBody PersonPutDto body
    ) {
        return ResponseEntity.ok(personService.put(id, body));
    }

    @Operation(
        summary = "Partially update person",
        description = "Updates one or more fields of an existing person (PATCH semantics)."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Person patched",
            content = @Content(schema = @Schema(implementation = PersonReadDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Person not found"
        )
    })
    @PatchMapping(path = "/{id}")
    public ResponseEntity<PersonReadDto> patch(
            @Parameter(description = "ID of the person to update", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Person patch payload (only fields to change)",
                required = true,
                content = @Content(schema = @Schema(implementation = PersonPatchDto.class))
            )
            @RequestBody JsonNode jsonBody
    ) {
        PersonPatchDto dto = objectMapper.convertValue(jsonBody, PersonPatchDto.class);
        Set<ConstraintViolation<PersonPatchDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
           throw new ConstraintViolationException(violations);
        }

        return ResponseEntity.ok(personService.patch(id, dto, jsonBody));
    }

    @Operation(
        summary = "Delete person",
        description = "Deletes a person by ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Person deleted"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Person not found"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the person to delete", required = true)
            @PathVariable Long id
    ) {
        personService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
