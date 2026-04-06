package com.shop.pricing.discount.application;

import java.util.UUID;

import com.shop.pricing.article.application.ArticleQueryService;
import com.shop.pricing.discount.domain.Discount;
import com.shop.pricing.pricequery.application.PricingMetrics;
import com.shop.pricing.shared.domain.BusinessConflictException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class DiscountCommandService {

    private final DiscountRepository discountRepository;
    private final ArticleQueryService articleQueryService;
    private final PricingMetrics pricingMetrics;

    public DiscountCommandService(DiscountRepository discountRepository,
                                  ArticleQueryService articleQueryService,
                                  PricingMetrics pricingMetrics) {
        this.discountRepository = discountRepository;
        this.articleQueryService = articleQueryService;
        this.pricingMetrics = pricingMetrics;
    }

    @Transactional
    @CacheEvict(cacheNames = {"articlePrices", "articlePriceList"}, allEntries = true)
    public Discount create(UUID articleId, CreateDiscountCommand command) {
        articleQueryService.getRequired(articleId);

        if (command.enabled()
                && discountRepository.existsEnabledOverlap(articleId, command.startDate(), command.endDate())) {
            pricingMetrics.markOverlapRejected();
            throw new BusinessConflictException(
                    "Discount date range overlaps with another enabled discount for article %s".formatted(articleId)
            );
        }

        Discount discount = new Discount(
                UUID.randomUUID(),
                articleId,
                command.discountType(),
                command.discountValue(),
                command.startDate(),
                command.endDate(),
                command.enabled(),
                null,
                null
        );
        Discount savedDiscount = discountRepository.save(discount);
        pricingMetrics.markDiscountCreated();
        return savedDiscount;
    }
}
