package com.luppol.lifebalance.adapter.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

public class FetchMetadataFilter extends OncePerRequestFilter {
    private static final String FETCH_SITE_HEADER = "Sec-Fetch-Site";
    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS");
    private static final Set<String> TRUSTED_SITES = Set.of("same-origin", "none");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (isCrossSiteWrite(request)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Cross-site requests may not change data");
            return;
        }
        chain.doFilter(request, response);
    }

    private static boolean isCrossSiteWrite(HttpServletRequest request) {
        String site = request.getHeader(FETCH_SITE_HEADER);
        return site != null && !SAFE_METHODS.contains(request.getMethod()) && !TRUSTED_SITES.contains(site);
    }
}
