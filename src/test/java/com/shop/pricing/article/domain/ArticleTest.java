package com.shop.pricing.article.domain;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import com.shop.pricing.shared.domain.BusinessValidationException;
import com.shop.pricing.shared.domain.Money;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArticleTest {

    private static final String VALID_NAME = "Widget";
    private static final String VALID_BRAND = "Acme";
    private static final String VALID_SLOGAN = "Best widget";
    private static final String VALID_COST_PRICE = "10.0000";
    private static final String VALID_BASE_SALE_PRICE = "15.0000";
    private static final String VALID_VAT_RATE = "0.2100";

    @Test
    void givenValidData_whenCreatingArticle_thenItSucceeds() {
        assertThatNoException().isThrownBy(this::createValidArticle);
    }

    @Test
    void givenBlankName_whenCreatingArticle_thenItFails() {
        String blankName = "";

        assertThatThrownBy(() -> article(blankName, VALID_BRAND, VALID_SLOGAN, VALID_COST_PRICE, VALID_BASE_SALE_PRICE, VALID_VAT_RATE))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("name");
    }

    @Test
    void givenBlankBrand_whenCreatingArticle_thenItFails() {
        String blankBrand = "  ";

        assertThatThrownBy(() -> article(VALID_NAME, blankBrand, VALID_SLOGAN, VALID_COST_PRICE, VALID_BASE_SALE_PRICE, VALID_VAT_RATE))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("brand");
    }

    @Test
    void givenBlankSlogan_whenCreatingArticle_thenItFails() {
        String blankSlogan = "";

        assertThatThrownBy(() -> article(VALID_NAME, VALID_BRAND, blankSlogan, VALID_COST_PRICE, VALID_BASE_SALE_PRICE, VALID_VAT_RATE))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("slogan");
    }

    @Test
    void givenNegativeCostPrice_whenCreatingArticle_thenItFails() {
        String negativeCostPrice = "-0.0100";

        assertThatThrownBy(() -> article(VALID_NAME, VALID_BRAND, VALID_SLOGAN, negativeCostPrice, VALID_BASE_SALE_PRICE, VALID_VAT_RATE))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("cost price");
    }

    @Test
    void givenNegativeBaseSalePrice_whenCreatingArticle_thenItFails() {
        String negativeBaseSalePrice = "-1.0000";

        assertThatThrownBy(() -> article(VALID_NAME, VALID_BRAND, VALID_SLOGAN, VALID_COST_PRICE, negativeBaseSalePrice, VALID_VAT_RATE))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("base sale price");
    }

    @Test
    void givenBaseSalePriceBelowCostPrice_whenCreatingArticle_thenItFails() {
        String lowerSalePrice = "9.9900";

        assertThatThrownBy(() -> article(VALID_NAME, VALID_BRAND, VALID_SLOGAN, VALID_COST_PRICE, lowerSalePrice, VALID_VAT_RATE))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("lower than cost price");
    }

    @Test
    void givenBaseSalePriceEqualToCostPrice_whenCreatingArticle_thenItSucceeds() {
        String equalSalePrice = VALID_COST_PRICE;

        assertThatNoException().isThrownBy(() ->
                article(VALID_NAME, VALID_BRAND, VALID_SLOGAN, VALID_COST_PRICE, equalSalePrice, VALID_VAT_RATE));
    }

    @Test
    void givenNegativeVatRate_whenCreatingArticle_thenItFails() {
        String negativeVatRate = "-0.0100";

        assertThatThrownBy(() -> article(VALID_NAME, VALID_BRAND, VALID_SLOGAN, VALID_COST_PRICE, VALID_BASE_SALE_PRICE, negativeVatRate))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("VAT rate");
    }

    @Test
    void givenVatRateAboveOne_whenCreatingArticle_thenItFails() {
        String vatRateAboveOne = "1.0100";

        assertThatThrownBy(() -> article(VALID_NAME, VALID_BRAND, VALID_SLOGAN, VALID_COST_PRICE, VALID_BASE_SALE_PRICE, vatRateAboveOne))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("VAT rate");
    }

    @Test
    void givenZeroVatRate_whenCreatingArticle_thenItSucceeds() {
        String zeroVatRate = "0.0000";

        assertThatNoException().isThrownBy(() ->
                article(VALID_NAME, VALID_BRAND, VALID_SLOGAN, VALID_COST_PRICE, VALID_BASE_SALE_PRICE, zeroVatRate));
    }

    @Test
    void givenVatRateOfOne_whenCreatingArticle_thenItSucceeds() {
        String maxVatRate = "1.0000";

        assertThatNoException().isThrownBy(() ->
                article(VALID_NAME, VALID_BRAND, VALID_SLOGAN, VALID_COST_PRICE, VALID_BASE_SALE_PRICE, maxVatRate));
    }

    private Article createValidArticle() {
        return article(VALID_NAME, VALID_BRAND, VALID_SLOGAN, VALID_COST_PRICE, VALID_BASE_SALE_PRICE, VALID_VAT_RATE);
    }

    private Article article(String name, String brand, String slogan, String costPrice, String salePrice, String vatRate) {
        return new Article(
                UUID.randomUUID(),
                name,
                brand,
                slogan,
                Money.of(new BigDecimal(costPrice)),
                Money.of(new BigDecimal(salePrice)),
                new BigDecimal(vatRate),
                true,
                null,
                null
        );
    }
}
