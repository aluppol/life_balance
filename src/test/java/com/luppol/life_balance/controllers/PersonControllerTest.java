package com.luppol.life_balance.controllers;

import com.luppol.life_balance.dto.PersonDto;
import com.luppol.life_balance.mappers.PersonMapper;
import com.luppol.life_balance.models.Person;
import com.luppol.life_balance.services.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;


@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    PersonService service;

    @MockitoBean
    PersonMapper mapper;

    @Test
    void create_successful() throws Exception {
        final long ID = 1L;
        final String FIRST_NAME = "John";
        final String LAST_NAME = "Show";

        Person person = Person.builder().id(ID).firstName(FIRST_NAME).lastName(LAST_NAME).build();
        when(service.create(any())).thenReturn(person);
        when(mapper.toDto(person)).thenReturn(new PersonDto(ID, FIRST_NAME, LAST_NAME, null, null, null, null));

        mvc.perform(post(PersonController.BASE_PATH)
                .contentType("application/json")
                .content("""
                        {"first_name": "John", "last_name": "Snow"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(ID));

    }

    // TODO the rest of the tests
}
