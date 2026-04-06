package com.shop.pricing.article.adapter.in.web;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;
import com.shop.pricing.article.application.ArticleCommandService;
import com.shop.pricing.article.application.ArticleQueryService;
import com.shop.pricing.article.application.CreateArticleCommand;
import com.shop.pricing.article.application.UpdateArticleCommand;
import com.shop.pricing.article.domain.Article;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleCommandService articleCommandService;
    private final ArticleQueryService articleQueryService;

    public ArticleController(ArticleCommandService articleCommandService,
                             ArticleQueryService articleQueryService) {
        this.articleCommandService = articleCommandService;
        this.articleQueryService = articleQueryService;
    }

    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(@Valid @RequestBody CreateArticleRequest request) {
        Article article = articleCommandService.create(new CreateArticleCommand(
                request.name(),
                request.brand(),
                request.slogan(),
                request.costPriceExclVat(),
                request.baseSalePriceExclVat(),
                request.vatRate(),
                request.active()
        ));
        return ResponseEntity.created(URI.create("/api/v1/articles/" + article.id()))
                .body(ArticleResponse.from(article));
    }

    @PutMapping("/{articleId}")
    public ArticleResponse updateArticle(@PathVariable UUID articleId, @Valid @RequestBody UpdateArticleRequest request) {
        Article article = articleCommandService.update(articleId, new UpdateArticleCommand(
                request.name(),
                request.brand(),
                request.slogan(),
                request.costPriceExclVat(),
                request.baseSalePriceExclVat(),
                request.vatRate(),
                request.active()
        ));
        return ArticleResponse.from(article);
    }

    @GetMapping("/{articleId}")
    public ArticleResponse getArticle(@PathVariable UUID articleId) {
        return ArticleResponse.from(articleQueryService.getRequired(articleId));
    }
}
