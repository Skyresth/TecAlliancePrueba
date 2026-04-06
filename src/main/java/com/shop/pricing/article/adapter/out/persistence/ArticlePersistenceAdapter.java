package com.shop.pricing.article.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.shop.pricing.article.application.ArticleRepository;
import com.shop.pricing.article.domain.Article;
import com.shop.pricing.shared.domain.Money;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;


@Component
public class ArticlePersistenceAdapter implements ArticleRepository {

    private final ArticleJpaRepository articleJpaRepository;

    public ArticlePersistenceAdapter(ArticleJpaRepository articleJpaRepository) {
        this.articleJpaRepository = articleJpaRepository;
    }

    @Override
    public Article save(Article article) {
        ArticleEntity entity = toEntity(article);
        ArticleEntity savedEntity = articleJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Article> findById(UUID articleId) {
        return articleJpaRepository.findById(articleId).map(this::toDomain);
    }

    @Override
    public List<Article> findAllActive(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("name").ascending().and(Sort.by("id").ascending()));
        return articleJpaRepository.findAllByActive(true, pageable).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public long countActive() {
        return articleJpaRepository.countByActive(true);
    }

    private ArticleEntity toEntity(Article article) {
        ArticleEntity entity = new ArticleEntity();
        entity.setId(article.id());
        entity.setName(article.name());
        entity.setBrand(article.brand());
        entity.setSlogan(article.slogan());
        entity.setCostPriceExclVat(article.costPriceExclVat().amount());
        entity.setBaseSalePriceExclVat(article.baseSalePriceExclVat().amount());
        entity.setVatRate(article.vatRate());
        entity.setActive(article.active());
        entity.setCreatedAt(article.createdAt());
        entity.setUpdatedAt(article.updatedAt());
        return entity;
    }

    private Article toDomain(ArticleEntity entity) {
        return new Article(
                entity.getId(),
                entity.getName(),
                entity.getBrand(),
                entity.getSlogan(),
                Money.of(entity.getCostPriceExclVat()),
                Money.of(entity.getBaseSalePriceExclVat()),
                entity.getVatRate(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
