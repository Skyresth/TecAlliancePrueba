package com.shop.pricing.pricequery.adapter.in.web;

import java.util.List;

import com.shop.pricing.pricequery.application.PriceListPage;

public record ArticlePricePageResponse(
        List<ArticlePriceResponse> content,
        long totalElements,
        int totalPages,
        int page,
        int size
) {

    public static ArticlePricePageResponse from(PriceListPage priceListPage) {
        List<ArticlePriceResponse> content = priceListPage.content().stream()
                .map(ArticlePriceResponse::from)
                .toList();
        return new ArticlePricePageResponse(
                content,
                priceListPage.totalElements(),
                priceListPage.totalPages(),
                priceListPage.page(),
                priceListPage.size()
        );
    }
}
