package com.luppol.lifebalance;

import org.springframework.context.ApplicationContext;

import java.io.PrintStream;

public interface OperatorCommand {
    void run(ApplicationContext context, PrintStream output);
}
