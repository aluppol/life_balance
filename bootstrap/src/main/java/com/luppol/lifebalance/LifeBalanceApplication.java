package com.luppol.lifebalance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LifeBalanceApplication {
    public static void main(String[] args) {
        if (DemoReset.isRequested(args)) {
            DemoReset.run(args);
        } else {
            SpringApplication.run(LifeBalanceApplication.class, args);
        }
    }
}
