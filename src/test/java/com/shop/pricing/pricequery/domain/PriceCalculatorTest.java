package com.shop.pricing.pricequery.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import com.shop.pricing.article.domain.Article;
import com.shop.pricing.discount.domain.Discount;
import com.shop.pricing.discount.domain.DiscountType;
import com.shop.pricing.shared.domain.Money;

import static org.assertj.core.api.Assertions.assertThat;

class PriceCalculatorTest {

    private static final String DEFAULT_NAME = "Widget";
    private static final String DEFAULT_BRAND = "Acme";
    private static final String DEFAULT_SLOGAN = "Best widget";
    private static final LocalDate EFFECTIVE_DATE = LocalDate.of(2021, 3, 15);
    private static final LocalDate DISCOUNT_START_DATE = LocalDate.of(2021, 3, 1);
    private static final LocalDate DISCOUNT_END_DATE = LocalDate.of(2021, 3, 31);

    private final PriceCalculator priceCalculator = new PriceCalculator();

    @Test
    void givenNoDiscount_whenCalculatingPrice_thenBaseSalePriceIsReturned() {
        Article article = article("10.0000", "15.0000", "0.2100");

        PriceCalculation result = priceCalculator.calculate(article, EFFECTIVE_DATE, Optional.empty());

        assertThat(result.effectiveSalePriceExclVat().amount()).isEqualByComparingTo("15.0000");
        assertThat(result.effectiveSalePriceInclVat().amount()).isEqualByComparingTo("18.1500");
        assertThat(result.floorApplied()).isFalse();
        assertThat(result.appliedDiscount()).isEmpty();
    }

    @Test
    void givenPercentageDiscountAboveCostFloor_whenCalculatingPrice_thenDiscountIsApplied() {
        Article article = article("10.0000", "15.0000", "0.2100");
        Discount discount = discount(DiscountType.PERCENTAGE, "0.1000");

        PriceCalculation result = priceCalculator.calculate(article, EFFECTIVE_DATE, Optional.of(discount));

        assertThat(result.effectiveSalePriceExclVat().amount()).isEqualByComparingTo("13.5000");
        assertThat(result.effectiveSalePriceInclVat().amount()).isEqualByComparingTo("16.3350");
        assertThat(result.floorApplied()).isFalse();
        assertThat(result.appliedDiscount()).contains(discount);
    }

    @Test
    void givenFixedAmountDiscountAboveCostFloor_whenCalculatingPrice_thenDiscountIsApplied() {
        Article article = article("10.0000", "20.0000", "0.2100");
        Discount discount = discount(DiscountType.FIXED_AMOUNT, "3.0000");

        PriceCalculation result = priceCalculator.calculate(article, EFFECTIVE_DATE, Optional.of(discount));

        assertThat(result.effectiveSalePriceExclVat().amount()).isEqualByComparingTo("17.0000");
        assertThat(result.effectiveSalePriceInclVat().amount()).isEqualByComparingTo("20.5700");
        assertThat(result.floorApplied()).isFalse();
    }

    @Test
    void givenFixedAmountDiscountBelowCostFloor_whenCalculatingPrice_thenFloorIsApplied() {
        Article article = article("10.0000", "12.0000", "0.2100");
        Discount discount = discount(DiscountType.FIXED_AMOUNT, "5.0000");

        PriceCalculation result = priceCalculator.calculate(article, EFFECTIVE_DATE, Optional.of(discount));

        assertThat(result.effectiveSalePriceExclVat().amount()).isEqualByComparingTo("10.0000");
        assertThat(result.effectiveSalePriceInclVat().amount()).isEqualByComparingTo("12.1000");
        assertThat(result.floorApplied()).isTrue();
    }

    @Test
    void givenHundredPercentDiscountAndPositiveCost_whenCalculatingPrice_thenFloorIsApplied() {
        Article article = article("10.0000", "15.0000", "0.2100");
        Discount discount = discount(DiscountType.PERCENTAGE, "1.0000");

        PriceCalculation result = priceCalculator.calculate(article, EFFECTIVE_DATE, Optional.of(discount));

        assertThat(result.effectiveSalePriceExclVat().amount()).isEqualByComparingTo("10.0000");
        assertThat(result.effectiveSalePriceInclVat().amount()).isEqualByComparingTo("12.1000");
        assertThat(result.floorApplied()).isTrue();
    }

    @Test
    void givenHundredPercentDiscountAndZeroCost_whenCalculatingPrice_thenFloorIsNotApplied() {
        Article article = article("0.0000", "10.0000", "0.2000");
        Discount discount = discount(DiscountType.PERCENTAGE, "1.0000");

        PriceCalculation result = priceCalculator.calculate(article, EFFECTIVE_DATE, Optional.of(discount));

        assertThat(result.effectiveSalePriceExclVat().amount()).isEqualByComparingTo("0.0000");
        assertThat(result.effectiveSalePriceInclVat().amount()).isEqualByComparingTo("0.0000");
        assertThat(result.floorApplied()).isFalse();
    }

    @Test
    void givenZeroVatRate_whenCalculatingPrice_thenInclusivePriceMatchesExclusivePrice() {
        Article article = article("5.0000", "10.0000", "0.0000");
        Discount discount = discount(DiscountType.FIXED_AMOUNT, "2.0000");

        PriceCalculation result = priceCalculator.calculate(article, EFFECTIVE_DATE, Optional.of(discount));

        assertThat(result.effectiveSalePriceExclVat().amount()).isEqualByComparingTo("8.0000");
        assertThat(result.effectiveSalePriceInclVat().amount()).isEqualByComparingTo("8.0000");
        assertThat(result.floorApplied()).isFalse();
    }

    @Test
    void givenArticle_whenCalculatingPrice_thenResultKeepsSameArticleInstance() {
        Article article = article("10.0000", "15.0000", "0.2100");

        PriceCalculation result = priceCalculator.calculate(article, EFFECTIVE_DATE, Optional.empty());

        assertThat(result.article()).isSameAs(article);
        assertThat(result.article().id()).isEqualTo(article.id());
        assertThat(result.baseSalePriceExclVat().amount()).isEqualByComparingTo("15.0000");
    }

    private Article article(String costPrice, String salePrice, String vatRate) {
        return new Article(
                UUID.randomUUID(),
                DEFAULT_NAME,
                DEFAULT_BRAND,
                DEFAULT_SLOGAN,
                Money.of(new BigDecimal(costPrice)),
                Money.of(new BigDecimal(salePrice)),
                new BigDecimal(vatRate),
                true,
                null,
                null
        );
    }

    private Discount discount(DiscountType discountType, String discountValue) {
        return new Discount(
                UUID.randomUUID(),
                UUID.randomUUID(),
                discountType,
                new BigDecimal(discountValue),
                DISCOUNT_START_DATE,
                DISCOUNT_END_DATE,
                true,
                null,
                null
        );
    }
}
