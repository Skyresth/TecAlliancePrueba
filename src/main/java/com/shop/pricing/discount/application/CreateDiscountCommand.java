package com.shop.pricing.discount.application;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.shop.pricing.discount.domain.DiscountType;


public record CreateDiscountCommand(
        DiscountType discountType,
        BigDecimal discountValue,
        LocalDate startDate,
        LocalDate endDate,
        boolean enabled
) {
}
