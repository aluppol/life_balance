package com.luppol.life_balance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.liquibase.enabled=false")
class LifeBalanceApplicationTests {

	@Test
	void contextLoads() {
		// no-op: just ensures Spring Boot context starts successfully
	}

}
