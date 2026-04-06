package com.shop.pricing.pricequery.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.shop.pricing.article.domain.Article;
import com.shop.pricing.discount.domain.Discount;
import com.shop.pricing.discount.domain.DiscountType;
import com.shop.pricing.pricequery.domain.PriceCalculation;

public record ArticlePriceResponse(
        UUID articleId,
        String name,
        String brand,
        String slogan,
        BigDecimal costPriceExclVat,
        BigDecimal baseSalePriceExclVat,
        BigDecimal vatRate,
        LocalDate effectiveAt,
        BigDecimal effectiveSalePriceExclVat,
        BigDecimal effectiveSalePriceInclVat,
        boolean floorApplied,
        DiscountSummary appliedDiscount
) {

    public static ArticlePriceResponse from(PriceCalculation priceCalculation) {
        Article article = priceCalculation.article();
        return new ArticlePriceResponse(
                article.id(),
                article.name(),
                article.brand(),
                article.slogan(),
                article.costPriceExclVat().amount(),
                priceCalculation.baseSalePriceExclVat().amount(),
                article.vatRate(),
                priceCalculation.effectiveAt(),
                priceCalculation.effectiveSalePriceExclVat().amount(),
                priceCalculation.effectiveSalePriceInclVat().amount(),
                priceCalculation.floorApplied(),
                priceCalculation.appliedDiscount().map(DiscountSummary::from).orElse(null)
        );
    }

    public record DiscountSummary(
            UUID id,
            DiscountType discountType,
            BigDecimal discountValue,
            LocalDate startDate,
            LocalDate endDate
    ) {
        public static DiscountSummary from(Discount discount) {
            return new DiscountSummary(
                    discount.id(),
                    discount.discountType(),
                    discount.discountValue(),
                    discount.startDate(),
                    discount.endDate()
            );
        }
    }
}
