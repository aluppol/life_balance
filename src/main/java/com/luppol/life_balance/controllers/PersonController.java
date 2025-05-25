package com.luppol.life_balance.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.luppol.life_balance.dto.PersonDto;
import com.luppol.life_balance.mappers.PersonMapper;
import com.luppol.life_balance.models.Person;
import com.luppol.life_balance.services.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(PersonController.BASE_PATH)
@RequiredArgsConstructor
public class PersonController {
    public static final String BASE_PATH = "/api/persons";

    private final PersonService personService;
    private final PersonMapper personMapper;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<PersonDto> create(@Valid @RequestBody PersonDto body) {
        Person saved = personService.create(body);
        return ResponseEntity
                .created(URI.create(BASE_PATH + "/" + saved.getId()))
                .body(personMapper.toDto(saved));
    }

    @GetMapping
    public List<PersonDto> getAll() {
        return personService.getAll().stream()
                .map(personMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public PersonDto getById(@PathVariable Long id) {
        return personMapper.toDto(personService.getById(id));
    }

    @PutMapping("/{id}")
    public PersonDto update(@PathVariable Long id,
                            @Valid @RequestBody PersonDto body) {
        return personMapper.toDto(personService.update(id, body));
    }

    @PatchMapping(path = "/{id}", consumes = "application/merge-patch+json")
    public PersonDto patch(
            @PathVariable Long id,
            @RequestBody JsonMergePatch mergePatch)
        throws JsonPatchException, JsonProcessingException {
        PersonDto currentDto = personMapper.toDto(personService.getById(id));
        JsonNode patchNode = mergePatch.apply(objectMapper.valueToTree(currentDto));
        PersonDto patchedDto = objectMapper.treeToValue(patchNode, PersonDto.class);

        return personMapper.toDto(personService.patch(id, patchedDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
