package com.shop.pricing.pricequery.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import com.shop.pricing.article.domain.Article;
import com.shop.pricing.discount.domain.Discount;
import com.shop.pricing.discount.domain.DiscountType;
import com.shop.pricing.shared.domain.Money;


public class PriceCalculator {
    public PriceCalculation calculate(Article article, LocalDate effectiveAt, Optional<Discount> discount) {
        Money discountedPrice = discount
                .map(currentDiscount -> applyDiscount(article.baseSalePriceExclVat(), currentDiscount))
                .orElse(article.baseSalePriceExclVat());

        Money effectivePrice = discountedPrice.max(article.costPriceExclVat());
        boolean floorApplied = effectivePrice.compareTo(discountedPrice) > 0;
        Money effectivePriceInclVat = effectivePrice.multiply(BigDecimal.ONE.add(article.vatRate()));

        return new PriceCalculation(
                article,
                effectiveAt,
                article.baseSalePriceExclVat(),
                effectivePrice,
                effectivePriceInclVat,
                floorApplied,
                discount
        );
    }

    private Money applyDiscount(Money baseSalePrice, Discount discount) {
        if (discount.discountType() == DiscountType.PERCENTAGE) {
            return baseSalePrice.subtract(baseSalePrice.multiply(discount.discountValue()));
        }
        return baseSalePrice.subtract(Money.of(discount.discountValue()));
    }
}
