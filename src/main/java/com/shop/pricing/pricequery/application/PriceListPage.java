package com.shop.pricing.pricequery.application;

import java.util.List;

import com.shop.pricing.pricequery.domain.PriceCalculation;


public record PriceListPage(
        List<PriceCalculation> content,
        long totalElements,
        int page,
        int size
) {

    /** Returns the total number of pages given the current page size. */
    public int totalPages() {
        return size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
    }
}
