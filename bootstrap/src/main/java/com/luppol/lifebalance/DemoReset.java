package com.luppol.lifebalance;

import com.luppol.lifebalance.application.person.PersonCommands;
import org.springframework.context.ApplicationContext;

import java.io.PrintStream;

public record DemoReset() implements OperatorCommand {
    public static final String NAME = "demo-reset";

    @Override
    public void run(ApplicationContext context, PrintStream output) {
        context.getBean(PersonCommands.class).resetGuestWorkspaces();
    }
}
