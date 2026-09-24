package com.luppol.lifebalance.domain.planning;

public record Tally(int planned, int completed) {
    public static final Tally NONE = new Tally(0, 0);

    public static Tally of(PlannedActivity activity) {
        return new Tally(1, activity.isCompleted() ? 1 : 0);
    }

    public Tally plus(Tally other) {
        return new Tally(planned + other.planned, completed + other.completed);
    }
}
