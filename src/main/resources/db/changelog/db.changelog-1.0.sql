--liquibase formatted sql

--changeset maksim:1
CREATE TABLE IF NOT EXISTS currency (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL
);

--changeset maksim:2
CREATE TABLE IF NOT EXISTS exchange_rate (
   id SERIAL PRIMARY KEY,
   base_currency_id BIGINT REFERENCES currency(id),
   target_currency_id BIGINT REFERENCES currency(id),
   rate NUMERIC
);