package com.shop.pricing.discount.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import com.shop.pricing.shared.domain.BusinessValidationException;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiscountTest {

    private static final UUID VALID_DISCOUNT_ID = UUID.randomUUID();
    private static final UUID VALID_ARTICLE_ID = UUID.randomUUID();
    private static final LocalDate VALID_START_DATE = LocalDate.of(2021, 3, 1);
    private static final LocalDate VALID_END_DATE = LocalDate.of(2021, 3, 31);
    private static final String VALID_PERCENTAGE_VALUE = "0.1000";
    private static final String VALID_FIXED_VALUE = "5.0000";

    @Test
    void givenValidPercentageDiscount_whenCreatingDiscount_thenItSucceeds() {
        assertThatNoException().isThrownBy(this::createValidPercentageDiscount);
    }

    @Test
    void givenValidFixedAmountDiscount_whenCreatingDiscount_thenItSucceeds() {
        assertThatNoException().isThrownBy(this::createValidFixedAmountDiscount);
    }

    @Test
    void givenNullArticleId_whenCreatingDiscount_thenItFails() {
        assertThatThrownBy(() -> discount(VALID_DISCOUNT_ID, null, DiscountType.PERCENTAGE, VALID_PERCENTAGE_VALUE, VALID_START_DATE, VALID_END_DATE))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("article id");
    }

    @Test
    void givenNullDiscountType_whenCreatingDiscount_thenItFails() {
        assertThatThrownBy(() -> discount(VALID_DISCOUNT_ID, VALID_ARTICLE_ID, null, VALID_PERCENTAGE_VALUE, VALID_START_DATE, VALID_END_DATE))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("type");
    }

    @Test
    void givenNullDiscountValue_whenCreatingDiscount_thenItFails() {
        assertThatThrownBy(() -> new Discount(
                VALID_DISCOUNT_ID,
                VALID_ARTICLE_ID,
                DiscountType.PERCENTAGE,
                null,
                VALID_START_DATE,
                VALID_END_DATE,
                true,
                null,
                null
        ))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("value");
    }

    @Test
    void givenZeroDiscountValue_whenCreatingDiscount_thenItFails() {
        assertThatThrownBy(() -> discount(VALID_DISCOUNT_ID, VALID_ARTICLE_ID, DiscountType.FIXED_AMOUNT, "0.0000", VALID_START_DATE, VALID_END_DATE))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    void givenNegativeDiscountValue_whenCreatingDiscount_thenItFails() {
        assertThatThrownBy(() -> discount(VALID_DISCOUNT_ID, VALID_ARTICLE_ID, DiscountType.FIXED_AMOUNT, "-1.0000", VALID_START_DATE, VALID_END_DATE))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    void givenPercentageDiscountAboveOne_whenCreatingDiscount_thenItFails() {
        assertThatThrownBy(() -> discount(VALID_DISCOUNT_ID, VALID_ARTICLE_ID, DiscountType.PERCENTAGE, "1.0001", VALID_START_DATE, VALID_END_DATE))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("0 and 1");
    }

    @Test
    void givenPercentageDiscountOfOne_whenCreatingDiscount_thenItSucceeds() {
        assertThatNoException().isThrownBy(() ->
                discount(VALID_DISCOUNT_ID, VALID_ARTICLE_ID, DiscountType.PERCENTAGE, "1.0000", VALID_START_DATE, VALID_END_DATE));
    }

    @Test
    void givenLargeFixedAmountDiscount_whenCreatingDiscount_thenItSucceeds() {
        assertThatNoException().isThrownBy(() ->
                discount(VALID_DISCOUNT_ID, VALID_ARTICLE_ID, DiscountType.FIXED_AMOUNT, "500.0000", VALID_START_DATE, VALID_END_DATE));
    }

    @Test
    void givenNullStartDate_whenCreatingDiscount_thenItFails() {
        assertThatThrownBy(() -> new Discount(
                VALID_DISCOUNT_ID,
                VALID_ARTICLE_ID,
                DiscountType.PERCENTAGE,
                new BigDecimal(VALID_PERCENTAGE_VALUE),
                null,
                VALID_END_DATE,
                true,
                null,
                null
        ))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("start and end dates");
    }

    @Test
    void givenNullEndDate_whenCreatingDiscount_thenItFails() {
        assertThatThrownBy(() -> new Discount(
                VALID_DISCOUNT_ID,
                VALID_ARTICLE_ID,
                DiscountType.PERCENTAGE,
                new BigDecimal(VALID_PERCENTAGE_VALUE),
                VALID_START_DATE,
                null,
                true,
                null,
                null
        ))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("start and end dates");
    }

    @Test
    void givenEndDateBeforeStartDate_whenCreatingDiscount_thenItFails() {
        assertThatThrownBy(() -> discount(VALID_DISCOUNT_ID, VALID_ARTICLE_ID, DiscountType.PERCENTAGE, VALID_PERCENTAGE_VALUE, VALID_END_DATE, VALID_START_DATE))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("end date");
    }

    @Test
    void givenSameDayRange_whenCreatingDiscount_thenItSucceeds() {
        assertThatNoException().isThrownBy(() ->
                discount(VALID_DISCOUNT_ID, VALID_ARTICLE_ID, DiscountType.PERCENTAGE, VALID_PERCENTAGE_VALUE, VALID_START_DATE, VALID_START_DATE));
    }

    private Discount createValidPercentageDiscount() {
        return discount(VALID_DISCOUNT_ID, VALID_ARTICLE_ID, DiscountType.PERCENTAGE, VALID_PERCENTAGE_VALUE, VALID_START_DATE, VALID_END_DATE);
    }

    private Discount createValidFixedAmountDiscount() {
        return discount(VALID_DISCOUNT_ID, VALID_ARTICLE_ID, DiscountType.FIXED_AMOUNT, VALID_FIXED_VALUE, VALID_START_DATE, VALID_END_DATE);
    }

    private Discount discount(UUID id, UUID articleId, DiscountType type, String value, LocalDate startDate, LocalDate endDate) {
        return new Discount(
                id,
                articleId,
                type,
                new BigDecimal(value),
                startDate,
                endDate,
                true,
                null,
                null
        );
    }
}
