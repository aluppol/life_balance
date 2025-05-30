package com.luppol.life_balance.mappers;

import java.util.Optional;
import java.util.function.Consumer;

public interface Mapper {
    static <T> void patch(Optional<T> field, Consumer<T> setter) {
        if(field == null) {
            setter.accept(field.orElse(null));
        }
    }
}
