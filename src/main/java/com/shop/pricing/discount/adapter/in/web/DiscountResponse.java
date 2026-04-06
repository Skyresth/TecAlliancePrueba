package com.shop.pricing.discount.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.shop.pricing.discount.domain.Discount;
import com.shop.pricing.discount.domain.DiscountType;


public record DiscountResponse(
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

    public static DiscountResponse from(Discount discount) {
        return new DiscountResponse(
                discount.id(),
                discount.articleId(),
                discount.discountType(),
                discount.discountValue(),
                discount.startDate(),
                discount.endDate(),
                discount.enabled(),
                discount.createdAt(),
                discount.updatedAt()
        );
    }
}
