package com.shop.pricing.article.application;

import java.math.BigDecimal;


public record CreateArticleCommand(
        String name,
        String brand,
        String slogan,
        BigDecimal costPriceExclVat,
        BigDecimal baseSalePriceExclVat,
        BigDecimal vatRate,
        boolean active
) {
}
