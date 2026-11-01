package com.luppol.lifebalance;

import com.luppol.lifebalance.application.person.PersonCommands;
import com.luppol.lifebalance.application.person.PersonQueries;
import com.luppol.lifebalance.domain.person.InactivityPeriod;
import org.springframework.context.ApplicationContext;

import java.io.PrintStream;

public record RemoveInactiveMembers(InactivityPeriod period) implements OperatorCommand {
    public static final String NAME = "remove-inactive-members";

    @Override
    public void run(ApplicationContext context, PrintStream output) {
        PersonQueries people = context.getBean(PersonQueries.class);
        new InactiveMembers(period).print(people.findInactiveMembers(period), output);
        context.getBean(PersonCommands.class).removeInactiveMembers(period);
        output.printf("Removed members inactive for at least %d days; members now: %d%n", period.days(),
                people.countMembers());
    }
}
