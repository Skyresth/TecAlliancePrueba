package com.shop.pricing.article.adapter.in.web;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;


public record CreateArticleRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 255) String brand,
        @NotBlank @Size(max = 500) String slogan,
        @NotNull @PositiveOrZero @Digits(integer = 15, fraction = 4) BigDecimal costPriceExclVat,
        @NotNull @PositiveOrZero @Digits(integer = 15, fraction = 4) BigDecimal baseSalePriceExclVat,
        @NotNull @DecimalMin("0.0") @DecimalMax("1.0") @Digits(integer = 1, fraction = 4) BigDecimal vatRate,
        @NotNull Boolean active
) {
}
