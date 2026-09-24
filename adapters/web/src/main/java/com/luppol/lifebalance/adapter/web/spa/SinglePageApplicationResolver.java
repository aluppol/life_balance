package com.luppol.lifebalance.adapter.web.spa;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.util.List;

public class SinglePageApplicationResolver extends PathResourceResolver {
    private static final String INDEX = "index.html";
    private static final List<String> SERVER_PATHS = List.of("api/", "actuator/");

    @Override
    protected Resource getResource(String resourcePath, Resource location) throws IOException {
        Resource requested = location.createRelative(resourcePath);
        if (requested.exists() && requested.isReadable()) {
            return requested;
        }
        return isClientRoute(resourcePath) ? existing(location.createRelative(INDEX)) : null;
    }

    private static boolean isClientRoute(String resourcePath) {
        return !resourcePath.contains(".") && SERVER_PATHS.stream().noneMatch(resourcePath::startsWith);
    }

    private static Resource existing(Resource resource) {
        return resource.exists() ? resource : null;
    }
}
