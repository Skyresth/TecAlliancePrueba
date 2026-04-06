package com.shop.pricing.discount.adapter.out.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface DiscountJpaRepository extends JpaRepository<DiscountEntity, UUID> {

    @Query("""
            select d
            from DiscountEntity d
            where d.articleId = :articleId
              and d.enabled = true
              and d.startDate <= :effectiveAt
              and d.endDate >= :effectiveAt
            order by d.startDate asc
            """)
    List<DiscountEntity> findApplicableDiscounts(@Param("articleId") UUID articleId,
                                                 @Param("effectiveAt") LocalDate effectiveAt);

    @Query("""
            select case when count(d) > 0 then true else false end
            from DiscountEntity d
            where d.articleId = :articleId
              and d.enabled = true
              and d.startDate <= :endDate
              and d.endDate >= :startDate
            """)
    boolean existsEnabledOverlap(@Param("articleId") UUID articleId,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);
}
