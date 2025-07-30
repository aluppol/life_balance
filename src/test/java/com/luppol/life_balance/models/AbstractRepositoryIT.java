package com.luppol.life_balance.models;

import com.luppol.life_balance.PostgresContainerBase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public abstract class AbstractRepositoryIT extends PostgresContainerBase { }
