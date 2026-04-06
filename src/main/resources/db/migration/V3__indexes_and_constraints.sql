CREATE INDEX idx_articles_active ON articles (active);
CREATE INDEX idx_article_discounts_article_id ON article_discounts (article_id);
CREATE INDEX idx_article_discounts_lookup ON article_discounts (article_id, enabled, start_date, end_date);
