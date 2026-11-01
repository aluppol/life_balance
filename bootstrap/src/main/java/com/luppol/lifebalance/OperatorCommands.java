package com.luppol.lifebalance;

import com.luppol.lifebalance.domain.person.InactivityPeriod;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class OperatorCommands {
    private static final Pattern DAYS = Pattern.compile("\\d{1,9}");
    private static final Map<String, Function<List<String>, OperatorCommand>> COMMANDS = Map.of(
            DemoReset.NAME, arguments -> new DemoReset(),
            InactiveMembers.NAME, arguments -> new InactiveMembers(inactivityPeriod(InactiveMembers.NAME, arguments)),
            RemoveInactiveMembers.NAME,
            arguments -> new RemoveInactiveMembers(inactivityPeriod(RemoveInactiveMembers.NAME, arguments)));

    private OperatorCommands() {
    }

    public static boolean isRequested(String[] args) {
        return args.length > 0 && COMMANDS.containsKey(args[0]);
    }

    public static void run(String[] args) {
        OperatorCommand command = parse(args);
        SpringApplication application = new SpringApplication(LifeBalanceApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        try (ConfigurableApplicationContext context = application.run(args)) {
            command.run(context, System.out);
        }
    }

    private static OperatorCommand parse(String[] args) {
        List<String> words = new DefaultApplicationArguments(args).getNonOptionArgs();
        return COMMANDS.get(words.getFirst()).apply(words.subList(1, words.size()));
    }

    private static InactivityPeriod inactivityPeriod(String command, List<String> arguments) {
        if (arguments.size() != 1 || !DAYS.matcher(arguments.getFirst()).matches()) {
            throw new IllegalArgumentException("Usage: %s <days>".formatted(command));
        }
        return new InactivityPeriod(Integer.parseInt(arguments.getFirst()));
    }
}
