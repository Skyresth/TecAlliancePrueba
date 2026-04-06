package com.shop.pricing.shared.infrastructure;

import com.shop.pricing.pricequery.domain.PriceCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DomainConfig {

    @Bean
    public PriceCalculator priceCalculator() {
        return new PriceCalculator();
    }
}
