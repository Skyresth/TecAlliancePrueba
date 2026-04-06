package com.shop.pricing.pricequery.domain;

import java.time.LocalDate;
import java.util.Optional;

import com.shop.pricing.article.domain.Article;
import com.shop.pricing.discount.domain.Discount;
import com.shop.pricing.shared.domain.Money;


public record PriceCalculation(
        Article article,
        LocalDate effectiveAt,
        Money baseSalePriceExclVat,
        Money effectiveSalePriceExclVat,
        Money effectiveSalePriceInclVat,
        boolean floorApplied,
        Optional<Discount> appliedDiscount
) {
}
