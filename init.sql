CREATE UNLOGGED TABLE IF NOT EXISTS payments (
    id             UUID PRIMARY KEY,
    correlation_id UUID           NOT NULL,
    amount         DECIMAL(10, 2) NOT NULL,
    requested_at   TIMESTAMP      NOT NULL,
    is_default     BOOLEAN        NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_payments_correlation_id ON payments(correlation_id);

CREATE INDEX IF NOT EXISTS idx_payments_requested_at ON payments(requested_at);

CREATE INDEX IF NOT EXISTS idx_payments_is_default ON payments(is_default);