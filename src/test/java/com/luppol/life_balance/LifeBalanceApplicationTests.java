package com.luppol.life_balance;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "spring.liquibase.enabled=false")
class LifeBalanceApplicationTests {

	@Test
	void main_delegatesToSpringApplicationRun() {
		// mock the static SpringApplication.run(..) call
		try (MockedStatic<SpringApplication> spring = mockStatic(SpringApplication.class)) {
			spring.when(() -> SpringApplication.run(eq(LifeBalanceApplication.class), any(String[].class)))
					.thenReturn(null);          // we don’t need a real context

			LifeBalanceApplication.main(new String[]{"--dummy"});

			// verify that main called SpringApplication.run(..)
			spring.verify(() -> SpringApplication.run(eq(LifeBalanceApplication.class), any(String[].class)));
		}
	}

	@Test
	void commandLineRunner_printsBeanNames() throws Exception {
		// minimal stub ApplicationContext
		ApplicationContext ctx = mock(ApplicationContext.class);
		when(ctx.getBeanDefinitionNames()).thenReturn(new String[]{"beanA", "beanB"});

		// obtain the runner bean and execute it
		CommandLineRunner runner = new LifeBalanceApplication().CommandLineRunner(ctx);
		runner.run();                       // no args required

		// at least one interaction proves the lambda body executed
		verify(ctx).getBeanDefinitionNames();
	}
}
