package com.shop.pricing.pricequery.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import com.shop.pricing.article.application.ArticleQueryService;
import com.shop.pricing.article.application.ArticleRepository;
import com.shop.pricing.article.domain.Article;
import com.shop.pricing.discount.application.DiscountRepository;
import com.shop.pricing.discount.domain.Discount;
import com.shop.pricing.discount.domain.DiscountType;
import com.shop.pricing.pricequery.domain.PriceCalculation;
import com.shop.pricing.pricequery.domain.PriceCalculator;
import com.shop.pricing.shared.domain.BusinessConflictException;
import com.shop.pricing.shared.domain.BusinessValidationException;
import com.shop.pricing.shared.domain.Money;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceQueryServiceTest {

    private static final UUID ARTICLE_ID = UUID.randomUUID();
    private static final LocalDate EFFECTIVE_DATE = LocalDate.of(2021, 3, 15);
    private static final LocalDate DISCOUNT_START_DATE = LocalDate.of(2021, 3, 1);
    private static final LocalDate DISCOUNT_END_DATE = LocalDate.of(2021, 3, 31);
    private static final String COST_PRICE = "10.0000";
    private static final String BASE_SALE_PRICE = "15.0000";
    private static final String VAT_RATE = "0.2100";

    @Test
    void givenInactiveArticle_whenGettingArticlePrice_thenItFails() {
        Article inactiveArticle = article(ARTICLE_ID, false);
        PriceQueryService service = service(List.of(inactiveArticle), List.of());

        assertThatThrownBy(() -> service.getArticlePrice(ARTICLE_ID, EFFECTIVE_DATE))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("Inactive");
    }

    @Test
    void givenMultipleApplicableDiscounts_whenGettingArticlePrice_thenItFails() {
        Article activeArticle = article(ARTICLE_ID, true);
        Discount firstDiscount = discount(ARTICLE_ID, DiscountType.PERCENTAGE, "0.1000");
        Discount secondDiscount = discount(ARTICLE_ID, DiscountType.FIXED_AMOUNT, "5.0000");
        PriceQueryService service = service(List.of(activeArticle), List.of(firstDiscount, secondDiscount));

        assertThatThrownBy(() -> service.getArticlePrice(ARTICLE_ID, EFFECTIVE_DATE))
                .isInstanceOf(BusinessConflictException.class)
                .hasMessageContaining("More than one discount");
    }

    @Test
    void givenNoApplicableDiscount_whenGettingArticlePrice_thenBasePriceIsReturned() {
        Article activeArticle = article(ARTICLE_ID, true);
        PriceQueryService service = service(List.of(activeArticle), List.of());

        PriceCalculation result = service.getArticlePrice(ARTICLE_ID, EFFECTIVE_DATE);

        assertThat(result.article()).isEqualTo(activeArticle);
        assertThat(result.appliedDiscount()).isEmpty();
        assertThat(result.floorApplied()).isFalse();
        assertThat(result.effectiveSalePriceInclVat().amount()).isEqualByComparingTo("18.1500");
    }

    @Test
    void givenOneApplicableDiscount_whenGettingArticlePrice_thenDiscountedPriceIsReturned() {
        Article activeArticle = article(ARTICLE_ID, true);
        Discount discount = discount(ARTICLE_ID, DiscountType.PERCENTAGE, "0.1000");
        PriceQueryService service = service(List.of(activeArticle), List.of(discount));

        PriceCalculation result = service.getArticlePrice(ARTICLE_ID, EFFECTIVE_DATE);

        assertThat(result.appliedDiscount()).contains(discount);
        assertThat(result.effectiveSalePriceExclVat().amount()).isEqualByComparingTo("13.5000");
    }

    @Test
    void givenPagedArticles_whenListingArticlePrices_thenPageMetadataAndContentAreReturned() {
        Article firstArticle = article(UUID.randomUUID(), true);
        Article secondArticle = secondArticle(UUID.randomUUID());
        ArticleRepository articleRepository = new StubArticleRepository(List.of(firstArticle, secondArticle), 7L);
        PriceQueryService service = service(articleRepository, List.of());

        PriceListPage result = service.listArticlePrices(EFFECTIVE_DATE, 1, 2);

        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(7L);
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.totalPages()).isEqualTo(4);
        assertThat(result.content().get(0).article().id()).isEqualTo(firstArticle.id());
        assertThat(result.content().get(1).article().id()).isEqualTo(secondArticle.id());
    }

    private PriceQueryService service(List<Article> articles, List<Discount> discountsForDate) {
        return service(new StubArticleRepository(articles, articles.size()), discountsForDate);
    }

    private PriceQueryService service(ArticleRepository articleRepository, List<Discount> discountsForDate) {
        ArticleQueryService articleQueryService = new ArticleQueryService(articleRepository);
        DiscountRepository discountRepository = new StubDiscountRepository(discountsForDate);
        PricingMetrics metrics = new PricingMetrics(new SimpleMeterRegistry());
        return new PriceQueryService(articleQueryService, articleRepository, discountRepository, new PriceCalculator(), metrics);
    }

    private Article article(UUID articleId, boolean active) {
        return new Article(
                articleId,
                "Widget",
                "Acme",
                "Best widget",
                Money.of(new BigDecimal(COST_PRICE)),
                Money.of(new BigDecimal(BASE_SALE_PRICE)),
                new BigDecimal(VAT_RATE),
                active,
                null,
                null
        );
    }

    private Article secondArticle(UUID articleId) {
        return new Article(
                articleId,
                "Alternator",
                "Acme",
                "Power when it matters",
                Money.of(new BigDecimal("20.0000")),
                Money.of(new BigDecimal("30.0000")),
                new BigDecimal("0.2100"),
                true,
                null,
                null
        );
    }

    private Discount discount(UUID articleId, DiscountType discountType, String discountValue) {
        return new Discount(
                UUID.randomUUID(),
                articleId,
                discountType,
                new BigDecimal(discountValue),
                DISCOUNT_START_DATE,
                DISCOUNT_END_DATE,
                true,
                null,
                null
        );
    }

    private record StubArticleRepository(List<Article> articles, long totalActive) implements ArticleRepository {

        @Override
        public Article save(Article article) {
            return article;
        }

        @Override
        public Optional<Article> findById(UUID articleId) {
            return articles.stream().filter(article -> article.id().equals(articleId)).findFirst();
        }

        @Override
        public List<Article> findAllActive(int page, int size) {
            return articles;
        }

        @Override
        public long countActive() {
            return totalActive;
        }
    }

    private record StubDiscountRepository(List<Discount> discounts) implements DiscountRepository {

        @Override
        public Discount save(Discount discount) {
            return discount;
        }

        @Override
        public List<Discount> findApplicableDiscounts(UUID articleId, LocalDate effectiveAt) {
            return discounts;
        }

        @Override
        public boolean existsEnabledOverlap(UUID articleId, LocalDate startDate, LocalDate endDate) {
            return false;
        }
    }
}
