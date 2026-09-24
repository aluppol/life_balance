package com.luppol.lifebalance.application.demo;

import com.luppol.lifebalance.domain.planning.Quadrant;

import java.time.DayOfWeek;
import java.util.Optional;

record DemoActivity(String title, String role, Optional<String> goal, Quadrant quadrant, Optional<DayOfWeek> day,
                    boolean isCompleted) {
}
