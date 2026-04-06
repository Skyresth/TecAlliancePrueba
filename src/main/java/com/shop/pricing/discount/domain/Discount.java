package com.shop.pricing.discount.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.shop.pricing.shared.domain.BusinessValidationException;


public record Discount(
        UUID id,
        UUID articleId,
        DiscountType discountType,
        BigDecimal discountValue,
        LocalDate startDate,
        LocalDate endDate,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {

    public Discount {
        if (id == null) {
            throw new IllegalArgumentException("Discount id must not be null");
        }
        if (articleId == null) {
            throw new BusinessValidationException("Discount article id must not be null");
        }
        if (discountType == null) {
            throw new BusinessValidationException("Discount type must not be null");
        }
        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Discount value must be greater than zero");
        }
        discountValue = discountValue.setScale(4, RoundingMode.HALF_UP);
        if (discountType == DiscountType.PERCENTAGE && discountValue.compareTo(BigDecimal.ONE) > 0) {
            throw new BusinessValidationException("Percentage discount value must be between 0 and 1");
        }
        if (startDate == null || endDate == null) {
            throw new BusinessValidationException("Discount start and end dates must not be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new BusinessValidationException("Discount end date must not be before start date");
        }
    }
}
