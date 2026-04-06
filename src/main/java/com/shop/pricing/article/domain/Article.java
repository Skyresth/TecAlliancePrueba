package com.shop.pricing.article.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

import com.shop.pricing.shared.domain.BusinessValidationException;
import com.shop.pricing.shared.domain.Money;


public record Article(
        UUID id,
        String name,
        String brand,
        String slogan,
        Money costPriceExclVat,
        Money baseSalePriceExclVat,
        BigDecimal vatRate,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    public Article {
        if (id == null) {
            throw new IllegalArgumentException("Article id must not be null");
        }
        if (name == null || name.isBlank()) {
            throw new BusinessValidationException("Article name must not be blank");
        }
        if (brand == null || brand.isBlank()) {
            throw new BusinessValidationException("Article brand must not be blank");
        }
        if (slogan == null || slogan.isBlank()) {
            throw new BusinessValidationException("Article slogan must not be blank");
        }
        if (costPriceExclVat == null || costPriceExclVat.isNegative()) {
            throw new BusinessValidationException("Article cost price must be zero or positive");
        }
        if (baseSalePriceExclVat == null || baseSalePriceExclVat.isNegative()) {
            throw new BusinessValidationException("Article base sale price must be zero or positive");
        }
        if (baseSalePriceExclVat.compareTo(costPriceExclVat) < 0) {
            throw new BusinessValidationException("Base sale price must not be lower than cost price");
        }
        if (vatRate == null) {
            throw new BusinessValidationException("VAT rate must not be null");
        }
        vatRate = vatRate.setScale(4, RoundingMode.HALF_UP);
        if (vatRate.compareTo(BigDecimal.ZERO) < 0 || vatRate.compareTo(BigDecimal.ONE) > 0) {
            throw new BusinessValidationException("VAT rate must be between 0 and 1");
        }
    }
}
