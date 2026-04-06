package com.shop.pricing.shared.infrastructure;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "app.rate-limit")
public record RateLimitProperties(
        long listCapacity,
        Duration listWindow,
        long priceCapacity,
        Duration priceWindow,
        long writeCapacity,
        Duration writeWindow
) {
}
