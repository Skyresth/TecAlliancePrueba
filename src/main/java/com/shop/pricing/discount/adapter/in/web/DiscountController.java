package com.shop.pricing.discount.adapter.in.web;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;
import com.shop.pricing.discount.application.CreateDiscountCommand;
import com.shop.pricing.discount.application.DiscountCommandService;
import com.shop.pricing.discount.domain.Discount;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/articles/{articleId}/discounts")
public class DiscountController {

    private final DiscountCommandService discountCommandService;

    public DiscountController(DiscountCommandService discountCommandService) {
        this.discountCommandService = discountCommandService;
    }

    @PostMapping
    public ResponseEntity<DiscountResponse> createDiscount(@PathVariable UUID articleId,
                                                           @Valid @RequestBody CreateDiscountRequest request) {
        Discount discount = discountCommandService.create(articleId, new CreateDiscountCommand(
                request.discountType(),
                request.discountValue(),
                request.startDate(),
                request.endDate(),
                request.enabled()
        ));
        return ResponseEntity.created(URI.create("/api/v1/articles/" + articleId + "/discounts/" + discount.id()))
                .body(DiscountResponse.from(discount));
    }
}
