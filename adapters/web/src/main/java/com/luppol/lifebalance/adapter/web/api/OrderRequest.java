package com.luppol.lifebalance.adapter.web.api;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public record OrderRequest(@NotNull List<@NotNull UUID> ids) {
    public <T> List<T> idsAs(Function<UUID, T> identifier) {
        return ids.stream().map(identifier).toList();
    }
}
