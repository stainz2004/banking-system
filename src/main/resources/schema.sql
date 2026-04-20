CREATE TABLE IF NOT EXISTS account
(
    id          BIGSERIAL PRIMARY KEY,
    customer_id VARCHAR(64)       NOT NULL,
    country     VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS account_balance
(
    id               BIGSERIAL PRIMARY KEY,
    account_id       BIGINT         NOT NULL REFERENCES account (id) ON DELETE CASCADE,
    currency         VARCHAR(3)    NOT NULL,
    available_amount NUMERIC(19, 4) NOT NULL DEFAULT 0,
    UNIQUE(account_id, currency)
);

CREATE INDEX IF NOT EXISTS idx_account_balance_account_id ON account_balance(account_id);

CREATE TABLE IF NOT EXISTS transactions
(
    id            BIGSERIAL PRIMARY KEY,
    account_id    BIGINT         NOT NULL REFERENCES account (id) ON DELETE CASCADE,
    amount        NUMERIC(19, 4) NOT NULL,
    currency      VARCHAR(3)     NOT NULL,
    direction     VARCHAR(10)    NOT NULL,
    description   VARCHAR(500)   NOT NULL,
    balance_after NUMERIC(19, 4) NOT NULL,
    created_at    TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_transactions_account_id ON transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_account_customer_id      ON account(customer_id);
