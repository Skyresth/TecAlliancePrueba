CREATE TABLE articles (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    slogan VARCHAR(500) NOT NULL,
    cost_price_excl_vat NUMERIC(19,4) NOT NULL,
    base_sale_price_excl_vat NUMERIC(19,4) NOT NULL,
    vat_rate NUMERIC(5,4) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_articles_cost_price_positive CHECK (cost_price_excl_vat >= 0),
    CONSTRAINT chk_articles_base_sale_positive CHECK (base_sale_price_excl_vat >= 0),
    CONSTRAINT chk_articles_base_sale_floor CHECK (base_sale_price_excl_vat >= cost_price_excl_vat),
    CONSTRAINT chk_articles_vat_rate CHECK (vat_rate >= 0 AND vat_rate <= 1)
);
