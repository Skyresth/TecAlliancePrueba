package com.shop.pricing.article.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArticleJpaRepository extends JpaRepository<ArticleEntity, UUID> {

    Page<ArticleEntity> findAllByActive(boolean active, Pageable pageable);

    long countByActive(boolean active);
}
