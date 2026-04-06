package com.shop.pricing.article.application;

import java.util.UUID;

import com.shop.pricing.article.domain.Article;
import com.shop.pricing.pricequery.application.PricingMetrics;
import com.shop.pricing.shared.domain.Money;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ArticleCommandService {

    private final ArticleRepository articleRepository;
    private final ArticleQueryService articleQueryService;
    private final PricingMetrics pricingMetrics;

    public ArticleCommandService(ArticleRepository articleRepository,
                                 ArticleQueryService articleQueryService,
                                 PricingMetrics pricingMetrics) {
        this.articleRepository = articleRepository;
        this.articleQueryService = articleQueryService;
        this.pricingMetrics = pricingMetrics;
    }

    @Transactional
    @CacheEvict(cacheNames = {"articleById", "articlePrices", "articlePriceList"}, allEntries = true)
    public Article create(CreateArticleCommand command) {
        Article article = new Article(
                UUID.randomUUID(),
                command.name(),
                command.brand(),
                command.slogan(),
                Money.of(command.costPriceExclVat()),
                Money.of(command.baseSalePriceExclVat()),
                command.vatRate(),
                command.active(),
                null,
                null
        );
        Article saved = articleRepository.save(article);
        pricingMetrics.markArticleCreated();
        return saved;
    }

    @Transactional
    @CacheEvict(cacheNames = {"articleById", "articlePrices", "articlePriceList"}, allEntries = true)
    public Article update(UUID articleId, UpdateArticleCommand command) {
        Article existing = articleQueryService.getRequired(articleId);
        Article updated = new Article(
                existing.id(),
                command.name(),
                command.brand(),
                command.slogan(),
                Money.of(command.costPriceExclVat()),
                Money.of(command.baseSalePriceExclVat()),
                command.vatRate(),
                command.active(),
                existing.createdAt(),
                existing.updatedAt()
        );
        return articleRepository.save(updated);
    }
}
