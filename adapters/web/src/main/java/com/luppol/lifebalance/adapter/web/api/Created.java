package com.luppol.lifebalance.adapter.web.api;

import org.springframework.http.ResponseEntity;

import java.net.URI;

public final class Created {
    private Created() {
    }

    public static <T> ResponseEntity<T> at(String path, T body) {
        return ResponseEntity.created(URI.create(path)).body(body);
    }
}
