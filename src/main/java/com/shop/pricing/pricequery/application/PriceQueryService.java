package com.shop.pricing.pricequery.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.shop.pricing.article.application.ArticleQueryService;
import com.shop.pricing.article.application.ArticleRepository;
import com.shop.pricing.article.domain.Article;
import com.shop.pricing.discount.application.DiscountRepository;
import com.shop.pricing.discount.domain.Discount;
import com.shop.pricing.pricequery.domain.PriceCalculation;
import com.shop.pricing.pricequery.domain.PriceCalculator;
import com.shop.pricing.shared.domain.BusinessConflictException;
import com.shop.pricing.shared.domain.BusinessValidationException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
public class PriceQueryService {

    private final ArticleQueryService articleQueryService;
    private final ArticleRepository articleRepository;
    private final DiscountRepository discountRepository;
    private final PriceCalculator priceCalculator;
    private final PricingMetrics pricingMetrics;

    public PriceQueryService(ArticleQueryService articleQueryService,
                             ArticleRepository articleRepository,
                             DiscountRepository discountRepository,
                             PriceCalculator priceCalculator,
                             PricingMetrics pricingMetrics) {
        this.articleQueryService = articleQueryService;
        this.articleRepository = articleRepository;
        this.discountRepository = discountRepository;
        this.priceCalculator = priceCalculator;
        this.pricingMetrics = pricingMetrics;
    }

    @Cacheable(cacheNames = "articlePrices", key = "#articleId.toString() + ':' + #effectiveAt")
    public PriceCalculation getArticlePrice(UUID articleId, LocalDate effectiveAt) {
        Article article = articleQueryService.getRequired(articleId);
        validateArticleIsActive(article);
        return calculate(article, effectiveAt);
    }

    @Cacheable(cacheNames = "articlePriceList", key = "#effectiveAt.toString() + ':p' + #page + ':s' + #size")
    public PriceListPage listArticlePrices(LocalDate effectiveAt, int page, int size) {
        List<PriceCalculation> prices = articleRepository.findAllActive(page, size).stream()
                .map(article -> calculate(article, effectiveAt))
                .toList();
        long totalElements = articleRepository.countActive();
        return new PriceListPage(prices, totalElements, page, size);
    }

    private PriceCalculation calculate(Article article, LocalDate effectiveAt) {
        List<Discount> applicableDiscounts = discountRepository.findApplicableDiscounts(article.id(), effectiveAt);
        if (applicableDiscounts.size() > 1) {
            throw new BusinessConflictException(
                    "More than one discount applies to article %s on %s".formatted(article.id(), effectiveAt)
            );
        }

        PriceCalculation priceCalculation = priceCalculator.calculate(
                article,
                effectiveAt,
                applicableDiscounts.stream().findFirst()
        );
        pricingMetrics.markPriceCalculated();
        if (priceCalculation.appliedDiscount().isPresent()) {
            pricingMetrics.markDiscountApplied();
        }
        if (priceCalculation.floorApplied()) {
            pricingMetrics.markFloorApplied();
        }
        return priceCalculation;
    }

    private void validateArticleIsActive(Article article) {
        if (!article.active()) {
            throw new BusinessValidationException("Inactive article cannot be priced");
        }
    }
}
