package com.shop.pricing.discount.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.shop.pricing.discount.domain.DiscountType;


public record CreateDiscountRequest(
        @NotNull DiscountType discountType,
        @NotNull @Positive @Digits(integer = 15, fraction = 4) BigDecimal discountValue,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull Boolean enabled
) {
}
