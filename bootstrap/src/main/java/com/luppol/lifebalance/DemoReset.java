package com.luppol.lifebalance;

import com.luppol.lifebalance.application.person.PersonCommands;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

public final class DemoReset {
    public static final String COMMAND = "demo-reset";

    private DemoReset() {
    }

    public static boolean isRequested(String[] args) {
        return args.length > 0 && COMMAND.equals(args[0]);
    }

    public static void run(String[] args) {
        SpringApplication application = new SpringApplication(LifeBalanceApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        try (ConfigurableApplicationContext context = application.run(args)) {
            context.getBean(PersonCommands.class).resetGuestWorkspaces();
        }
    }
}
