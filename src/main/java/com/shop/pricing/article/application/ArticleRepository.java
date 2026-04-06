package com.shop.pricing.article.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.shop.pricing.article.domain.Article;


public interface ArticleRepository {

    Article save(Article article);

    Optional<Article> findById(UUID articleId);

    List<Article> findAllActive(int page, int size);

    /** Returns the total number of active articles (used to build pagination metadata). */
    long countActive();
}
