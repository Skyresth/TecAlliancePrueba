package com.shop.pricing.discount.adapter.out.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.shop.pricing.discount.application.DiscountRepository;
import com.shop.pricing.discount.domain.Discount;
import org.springframework.stereotype.Component;


@Component
public class DiscountPersistenceAdapter implements DiscountRepository {

    private final DiscountJpaRepository discountJpaRepository;

    public DiscountPersistenceAdapter(DiscountJpaRepository discountJpaRepository) {
        this.discountJpaRepository = discountJpaRepository;
    }

    @Override
    public Discount save(Discount discount) {
        DiscountEntity entity = toEntity(discount);
        DiscountEntity savedEntity = discountJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public List<Discount> findApplicableDiscounts(UUID articleId, LocalDate effectiveAt) {
        return discountJpaRepository.findApplicableDiscounts(articleId, effectiveAt).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsEnabledOverlap(UUID articleId, LocalDate startDate, LocalDate endDate) {
        return discountJpaRepository.existsEnabledOverlap(articleId, startDate, endDate);
    }

    private DiscountEntity toEntity(Discount discount) {
        DiscountEntity entity = new DiscountEntity();
        entity.setId(discount.id());
        entity.setArticleId(discount.articleId());
        entity.setDiscountType(discount.discountType());
        entity.setDiscountValue(discount.discountValue());
        entity.setStartDate(discount.startDate());
        entity.setEndDate(discount.endDate());
        entity.setEnabled(discount.enabled());
        entity.setCreatedAt(discount.createdAt());
        entity.setUpdatedAt(discount.updatedAt());
        return entity;
    }

    private Discount toDomain(DiscountEntity entity) {
        return new Discount(
                entity.getId(),
                entity.getArticleId(),
                entity.getDiscountType(),
                entity.getDiscountValue(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isEnabled(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
