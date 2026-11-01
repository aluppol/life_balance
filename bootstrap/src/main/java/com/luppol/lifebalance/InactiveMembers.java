package com.luppol.lifebalance;

import com.luppol.lifebalance.application.person.PersonQueries;
import com.luppol.lifebalance.domain.person.InactivityPeriod;
import com.luppol.lifebalance.domain.person.MemberActivity;
import org.springframework.context.ApplicationContext;

import java.io.PrintStream;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record InactiveMembers(InactivityPeriod period) implements OperatorCommand {
    public static final String NAME = "inactive-members";

    @Override
    public void run(ApplicationContext context, PrintStream output) {
        PersonQueries people = context.getBean(PersonQueries.class);
        output.printf("Members: %d%n", people.countMembers());
        print(people.findInactiveMembers(period), output);
    }

    void print(List<MemberActivity> members, PrintStream output) {
        output.printf("Inactive for at least %d days: %d%n", period.days(), members.size());
        members.forEach(member -> output.printf("%s  last active %s%n", member.id(),
                member.lastActiveAt().truncatedTo(ChronoUnit.SECONDS)));
    }
}
