package com.luppol.lifebalance.domain.planning;

import com.luppol.lifebalance.domain.role.LifeRoleId;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record WeekScorecard(WeekStart week, Tally overall, Tally bigRocks, Map<Quadrant, Tally> byQuadrant,
                            Map<LifeRoleId, Tally> byRole) {
    public WeekScorecard {
        byQuadrant = Map.copyOf(byQuadrant);
        byRole = Map.copyOf(byRole);
    }

    public static WeekScorecard of(WeekStart week, List<PlannedActivity> activities) {
        return new WeekScorecard(
                week,
                tally(activities),
                tally(activities.stream().filter(PlannedActivity::isBigRock).toList()),
                tallyByQuadrant(activities),
                tallyByRole(activities));
    }

    private static Tally tally(List<PlannedActivity> activities) {
        return activities.stream().map(Tally::of).reduce(Tally.NONE, Tally::plus);
    }

    private static Map<Quadrant, Tally> tallyByQuadrant(List<PlannedActivity> activities) {
        return activities.stream().collect(Collectors.groupingBy(
                activity -> activity.details().quadrant(),
                () -> new EnumMap<>(Quadrant.class),
                Collectors.reducing(Tally.NONE, Tally::of, Tally::plus)));
    }

    private static Map<LifeRoleId, Tally> tallyByRole(List<PlannedActivity> activities) {
        Function<PlannedActivity, LifeRoleId> role = activity -> activity.details().roleId();
        return activities.stream().collect(Collectors.groupingBy(
                role,
                LinkedHashMap::new,
                Collectors.reducing(Tally.NONE, Tally::of, Tally::plus)));
    }
}
