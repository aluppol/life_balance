package com.luppol.life_balance.controllers;

import com.luppol.life_balance.dto.PersonCreateDto;
import com.luppol.life_balance.dto.PersonPutDto;
import com.luppol.life_balance.dto.PersonPatchDto;
import com.luppol.life_balance.dto.PersonReadDto;
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

    @PostMapping
    public ResponseEntity<PersonReadDto> create(@Valid @RequestBody PersonCreateDto body) {
        PersonReadDto saved = personService.create(body);
        return ResponseEntity
                .created(URI.create(BASE_PATH + "/" + saved.id()))
                .body(saved);
    }

    @GetMapping
    public ResponseEntity<List<PersonReadDto>> getAll() {
        return ResponseEntity.ok(personService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonReadDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonReadDto> put(@PathVariable Long id,
                            @Valid @RequestBody PersonPutDto body) {
        return ResponseEntity.ok(personService.put(id, body));
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<PersonReadDto> patch(
            @PathVariable Long id,
            @Valid @RequestBody PersonPatchDto body) {
        return ResponseEntity.ok(personService.patch(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
