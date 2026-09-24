package com.luppol.lifebalance.application.demo;

import com.luppol.lifebalance.domain.goal.GoalStatus;

import java.util.List;
import java.util.Optional;

record DemoGoal(String title, String role, String description, Optional<Integer> dueInWeeks, List<String> values,
                GoalStatus status) {
}
