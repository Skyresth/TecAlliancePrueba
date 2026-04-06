package com.shop.pricing.article.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.shop.pricing.article.domain.Article;


public record ArticleResponse(
        UUID id,
        String name,
        String brand,
        String slogan,
        BigDecimal costPriceExclVat,
        BigDecimal baseSalePriceExclVat,
        BigDecimal vatRate,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    public static ArticleResponse from(Article article) {
        return new ArticleResponse(
                article.id(),
                article.name(),
                article.brand(),
                article.slogan(),
                article.costPriceExclVat().amount(),
                article.baseSalePriceExclVat().amount(),
                article.vatRate(),
                article.active(),
                article.createdAt(),
                article.updatedAt()
        );
    }
}
