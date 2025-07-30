package com.luppol.life_balance.controllers;

import com.luppol.life_balance.PostgresContainerBase;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("stub")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class AbstractControllerIT  extends PostgresContainerBase {
    @Autowired
    MockMvc mvc;
}
