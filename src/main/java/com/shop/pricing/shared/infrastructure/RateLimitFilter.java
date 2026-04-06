package com.shop.pricing.shared.infrastructure;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String LIST = "list";
    private static final String PRICE = "price";
    private static final String WRITE = "write";

    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;
    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
            .maximumSize(20_000)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    public RateLimitFilter(RateLimitProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/v1/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String policy = resolvePolicy(request);
        String clientKey = request.getRemoteAddr() + ":" + policy;
        Bucket bucket = buckets.get(clientKey, key -> createBucket(policy));

        if (!bucket.tryConsume(1)) {
            response.setStatus(429);
            response.setContentType("application/json");
            ApiError apiError = new ApiError(
                    OffsetDateTime.now(),
                    429,
                    "RATE_LIMIT_EXCEEDED",
                    "Rate limit exceeded for " + policy + " requests",
                    request.getRequestURI(),
                    (String) request.getAttribute(TraceIdFilter.TRACE_ID)
            );
            objectMapper.writeValue(response.getOutputStream(), apiError);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Bucket createBucket(String policy) {
        return Bucket.builder()
                .addLimit(resolveBandwidth(policy))
                .build();
    }

    private Bandwidth resolveBandwidth(String policy) {
        return switch (policy) {
            case LIST -> Bandwidth.classic(
                    properties.listCapacity(),
                    Refill.intervally(properties.listCapacity(), properties.listWindow())
            );
            case PRICE -> Bandwidth.classic(
                    properties.priceCapacity(),
                    Refill.intervally(properties.priceCapacity(), properties.priceWindow())
            );
            default -> Bandwidth.classic(
                    properties.writeCapacity(),
                    Refill.intervally(properties.writeCapacity(), properties.writeWindow())
            );
        };
    }

    private String resolvePolicy(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        if ("GET".equalsIgnoreCase(method) && "/api/v1/articles".equals(uri)) {
            return LIST;
        }
        // Matches both GET /api/v1/articles/{id} and GET /api/v1/articles/{id}/price
        if ("GET".equalsIgnoreCase(method) && uri.matches("^/api/v1/articles/[^/]+(/price)?$")) {
            return PRICE;
        }
        return WRITE;
    }
}
