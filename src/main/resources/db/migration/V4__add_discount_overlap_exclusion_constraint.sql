CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE article_discounts
    ADD CONSTRAINT no_overlapping_enabled_discounts
    EXCLUDE USING GIST (
        article_id WITH =,
        daterange(start_date, end_date, '[]') WITH &&
    ) WHERE (enabled = true);
