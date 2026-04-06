package com.shop.pricing.pricequery.adapter.in.web;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import com.shop.pricing.pricequery.application.PriceQueryService;
import com.shop.pricing.pricequery.domain.PriceCalculation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/articles")
public class PriceQueryController {

    private final PriceQueryService priceQueryService;

    public PriceQueryController(PriceQueryService priceQueryService) {
        this.priceQueryService = priceQueryService;
    }

    @GetMapping("/{articleId}/price")
    public ArticlePriceResponse getArticlePrice(@PathVariable UUID articleId,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                LocalDate effectiveAt) {
        PriceCalculation priceCalculation = priceQueryService.getArticlePrice(articleId, effectiveAt);
        return ArticlePriceResponse.from(priceCalculation);
    }

    @GetMapping(params = "effectiveAt")
    public ArticlePricePageResponse listArticlePrices(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveAt,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ArticlePricePageResponse.from(priceQueryService.listArticlePrices(effectiveAt, page, size));
    }
}
