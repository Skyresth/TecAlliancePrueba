package com.shop.pricing.discount.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.shop.pricing.discount.domain.Discount;


public interface DiscountRepository {

    Discount save(Discount discount);

    List<Discount> findApplicableDiscounts(UUID articleId, LocalDate effectiveAt);

    boolean existsEnabledOverlap(UUID articleId, LocalDate startDate, LocalDate endDate);
}
