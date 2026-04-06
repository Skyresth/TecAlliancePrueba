package com.shop.pricing.shared.infrastructure;

import java.time.OffsetDateTime;


public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String code,
        String message,
        String path,
        String traceId
) {
}
