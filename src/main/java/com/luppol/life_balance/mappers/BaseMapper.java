package com.luppol.life_balance.mappers;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.Consumer;

public interface BaseMapper {
    default <T> void patch(JsonNode json, String field, T value, Consumer<T> setter) {
        if (json.has(field)) {
           setter.accept(value);
        }
    }
}
