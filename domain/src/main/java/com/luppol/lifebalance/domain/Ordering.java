package com.luppol.lifebalance.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.ToIntFunction;

public final class Ordering {
    private Ordering() {
    }

    public static <T> int nextPosition(Collection<T> ordered, ToIntFunction<T> position) {
        return ordered.stream().mapToInt(position).max().orElse(-1) + 1;
    }

    public static <K> void requirePermutation(List<K> requested, Collection<K> existing) {
        boolean isSameSize = requested.size() == existing.size();
        if (!isSameSize || !new HashSet<>(requested).equals(new HashSet<>(existing))) {
            throw new RuleViolationException("The new order must list every item exactly once");
        }
    }
}
