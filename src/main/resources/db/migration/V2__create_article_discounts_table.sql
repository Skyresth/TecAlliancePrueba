CREATE TABLE article_discounts (
    id UUID PRIMARY KEY,
    article_id UUID NOT NULL,
    discount_type VARCHAR(30) NOT NULL,
    discount_value NUMERIC(19,4) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    enabled BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_article_discounts_article FOREIGN KEY (article_id) REFERENCES articles (id),
    CONSTRAINT chk_discount_value_positive CHECK (discount_value > 0),
    CONSTRAINT chk_discount_date_range CHECK (end_date >= start_date),
    CONSTRAINT chk_discount_type CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT'))
);
