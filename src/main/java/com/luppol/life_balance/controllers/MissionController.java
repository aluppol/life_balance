package com.luppol.life_balance.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luppol.life_balance.dto.*;
import com.luppol.life_balance.security.AuthContext;
import com.luppol.life_balance.services.MissionAssignmentService;
import com.luppol.life_balance.services.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping(MissionController.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Mission", description = "Mission management API")
public class MissionController {
    public static final String BASE_PATH = "api/mission";

    private final MissionService missionService;
    private final MissionAssignmentService missionAssignmentService;
    private final AuthContext authContext;
    private final ObjectMapper objectMapper;

    @Autowired
    private Validator validator;

    @Operation(
            summary = "Create a new Mission",
            description = "Creates a new mission record and returns the created mission.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Mission created",
                            content = @Content(schema = @Schema(implementation = MissionReadDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    public ResponseEntity<MissionReadDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Mission creation payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MissionCreateDto.class))
            )
            @Valid @RequestBody MissionCreateDto body) {
        MissionReadDto saved = missionAssignmentService.createAndAssignToPerson(authContext.personId(), body);
        return ResponseEntity
                .created(URI.create(BASE_PATH + "/" + saved.id()))
                .body(saved);
    }

    @Operation(
            summary = "Get mission by ID",
            description = "Returns the mission with the specified ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Mission found",
                    content = @Content(schema = @Schema(implementation = MissionReadDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Mission not found"
            )
    })
    public ResponseEntity<MissionReadDto> getById(
            @Parameter(description = "ID of the mission to retrieve", required = true)
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(missionService.getById(id));
    }

    @Operation(
            summary = "Replace a Mission",
            description = "Fully updates an existing mission (PUT semantics)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Mission updated",
                    content = @Content(schema = @Schema(implementation = MissionReadDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Mission not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<MissionReadDto> put(
            @Parameter(description = "ID of the mission to update", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Mission replacement payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MissionPutDto.class))
            )
            @Valid @RequestBody MissionPutDto body) {
        MissionReadDto updated = missionService.put(id, body);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Partially update mission",
            description = "Updates one or more fields of an existing mission (PATCH semantics)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Mission patched",
                    content = @Content(schema = @Schema(implementation = MissionReadDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Mission not found"
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<MissionReadDto> patch(
            @Parameter(description = "ID of the mission to patch", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Mission patch payload (only fields to change)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MissionPatchDto.class))
            )
            @RequestBody JsonNode jsonBody) {
        MissionPatchDto dto = objectMapper.convertValue(jsonBody, MissionPatchDto.class);
        Set<ConstraintViolation<MissionPatchDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return ResponseEntity.ok(missionService.patch(id, dto, jsonBody));
    }

    @Operation(
            summary = "Delete mission",
            description = "Deletes a mission by ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Mission deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Mission not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the mission to delete", required = true)
            @PathVariable Long id
    ) {
        missionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
