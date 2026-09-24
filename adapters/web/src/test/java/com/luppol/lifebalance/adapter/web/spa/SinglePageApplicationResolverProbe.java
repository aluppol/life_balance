package com.luppol.lifebalance.adapter.web.spa;

import org.springframework.core.io.Resource;

import java.io.IOException;

class SinglePageApplicationResolverProbe extends SinglePageApplicationResolver {
    Resource resolve(String resourcePath, Resource location) throws IOException {
        return getResource(resourcePath, location);
    }
}
