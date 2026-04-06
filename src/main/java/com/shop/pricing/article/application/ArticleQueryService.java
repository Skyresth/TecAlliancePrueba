package com.shop.pricing.article.application;

import java.util.UUID;

import com.shop.pricing.article.domain.Article;
import com.shop.pricing.shared.domain.NotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
public class ArticleQueryService {

    private final ArticleRepository articleRepository;

    public ArticleQueryService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Cacheable(cacheNames = "articleById", key = "#articleId")
    public Article getRequired(UUID articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("Article %s was not found".formatted(articleId)));
    }
}
