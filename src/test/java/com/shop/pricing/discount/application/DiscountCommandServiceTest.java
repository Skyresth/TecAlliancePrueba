package com.shop.pricing.discount.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import com.shop.pricing.article.application.ArticleQueryService;
import com.shop.pricing.article.application.ArticleRepository;
import com.shop.pricing.article.domain.Article;
import com.shop.pricing.discount.domain.Discount;
import com.shop.pricing.discount.domain.DiscountType;
import com.shop.pricing.pricequery.application.PricingMetrics;
import com.shop.pricing.shared.domain.BusinessConflictException;
import com.shop.pricing.shared.domain.Money;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiscountCommandServiceTest {

    private static final UUID ARTICLE_ID = UUID.randomUUID();
    private static final LocalDate START_DATE = LocalDate.of(2021, 3, 1);
    private static final LocalDate END_DATE = LocalDate.of(2021, 3, 31);
    private static final String DISCOUNT_VALUE = "0.1500";

    @Test
    void givenOverlappingEnabledDiscount_whenCreatingDiscount_thenItFails() {
        DiscountRepository discountRepository = new OverlappingDiscountRepository();
        DiscountCommandService service = service(discountRepository);

        assertThatThrownBy(() -> service.create(ARTICLE_ID, createDiscountCommand()))
                .isInstanceOf(BusinessConflictException.class)
                .hasMessageContaining("overlaps");
    }

    @Test
    void givenNonOverlappingDiscount_whenCreatingDiscount_thenItIsSaved() {
        SavingDiscountRepository discountRepository = new SavingDiscountRepository();
        DiscountCommandService service = service(discountRepository);

        Discount result = service.create(ARTICLE_ID, createDiscountCommand());

        assertThat(result.articleId()).isEqualTo(ARTICLE_ID);
        assertThat(result.discountType()).isEqualTo(DiscountType.PERCENTAGE);
        assertThat(result.discountValue()).isEqualByComparingTo(DISCOUNT_VALUE);
        assertThat(discountRepository.savedDiscount()).isEqualTo(result);
    }

    private DiscountCommandService service(DiscountRepository discountRepository) {
        ArticleRepository articleRepository = new InMemoryArticleRepository(article(ARTICLE_ID));
        ArticleQueryService articleQueryService = new ArticleQueryService(articleRepository);
        PricingMetrics pricingMetrics = new PricingMetrics(new SimpleMeterRegistry());
        return new DiscountCommandService(discountRepository, articleQueryService, pricingMetrics);
    }

    private CreateDiscountCommand createDiscountCommand() {
        return new CreateDiscountCommand(
                DiscountType.PERCENTAGE,
                new BigDecimal(DISCOUNT_VALUE),
                START_DATE,
                END_DATE,
                true
        );
    }

    private Article article(UUID articleId) {
        return new Article(
                articleId,
                "Widget",
                "Acme",
                "Best widget",
                Money.of(new BigDecimal("10.0000")),
                Money.of(new BigDecimal("15.0000")),
                new BigDecimal("0.2100"),
                true,
                null,
                null
        );
    }

    private record InMemoryArticleRepository(Article article) implements ArticleRepository {

        @Override
        public Article save(Article article) {
            return article;
        }

        @Override
        public Optional<Article> findById(UUID articleId) {
            return article.id().equals(articleId) ? Optional.of(article) : Optional.empty();
        }

        @Override
        public List<Article> findAllActive(int page, int size) {
            return List.of();
        }

        @Override
        public long countActive() {
            return 0L;
        }
    }

    private static final class OverlappingDiscountRepository implements DiscountRepository {

        @Override
        public Discount save(Discount discount) {
            return discount;
        }

        @Override
        public List<Discount> findApplicableDiscounts(UUID articleId, LocalDate effectiveAt) {
            return List.of();
        }

        @Override
        public boolean existsEnabledOverlap(UUID articleId, LocalDate startDate, LocalDate endDate) {
            return true;
        }
    }

    private static final class SavingDiscountRepository implements DiscountRepository {

        private Discount savedDiscount;

        @Override
        public Discount save(Discount discount) {
            this.savedDiscount = discount;
            return discount;
        }

        @Override
        public List<Discount> findApplicableDiscounts(UUID articleId, LocalDate effectiveAt) {
            return List.of();
        }

        @Override
        public boolean existsEnabledOverlap(UUID articleId, LocalDate startDate, LocalDate endDate) {
            return false;
        }

        private Discount savedDiscount() {
            return savedDiscount;
        }
    }
}
