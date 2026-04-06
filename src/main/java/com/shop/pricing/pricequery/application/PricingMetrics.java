package com.shop.pricing.pricequery.application;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;


@Component
public class PricingMetrics {

    private final Counter articleCreatedCounter;
    private final Counter discountCreatedCounter;
    private final Counter priceCalculationCounter;
    private final Counter discountAppliedCounter;
    private final Counter floorAppliedCounter;
    private final Counter overlapRejectedCounter;

    public PricingMetrics(MeterRegistry meterRegistry) {
        this.articleCreatedCounter = meterRegistry.counter("pricing.article.created.total");
        this.discountCreatedCounter = meterRegistry.counter("pricing.discount.created.total");
        this.priceCalculationCounter = meterRegistry.counter("pricing.price.calculation.total");
        this.discountAppliedCounter = meterRegistry.counter("pricing.discount.applied.total");
        this.floorAppliedCounter = meterRegistry.counter("pricing.discount.floor.applied.total");
        this.overlapRejectedCounter = meterRegistry.counter("pricing.discount.overlap.rejected.total");
    }

    public void markArticleCreated() {
        articleCreatedCounter.increment();
    }

    public void markDiscountCreated() {
        discountCreatedCounter.increment();
    }

    public void markPriceCalculated() {
        priceCalculationCounter.increment();
    }

    public void markDiscountApplied() {
        discountAppliedCounter.increment();
    }

    public void markFloorApplied() {
        floorAppliedCounter.increment();
    }

    public void markOverlapRejected() {
        overlapRejectedCounter.increment();
    }
}
