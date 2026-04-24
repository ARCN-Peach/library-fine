CREATE TABLE fines (
    fine_id      UUID        NOT NULL,
    rental_id    UUID        NOT NULL,
    user_id      UUID        NOT NULL,
    amount       NUMERIC(10, 2) NOT NULL,
    currency     VARCHAR(3)  NOT NULL,
    status       VARCHAR(20) NOT NULL,
    generated_at TIMESTAMPTZ NOT NULL,
    paid_at      TIMESTAMPTZ,

    CONSTRAINT pk_fines PRIMARY KEY (fine_id),
    CONSTRAINT uq_fines_rental_id UNIQUE (rental_id),
    CONSTRAINT chk_fines_status CHECK (status IN ('PENDING', 'PAID')),
    CONSTRAINT chk_fines_amount CHECK (amount >= 0)
);

CREATE INDEX idx_fines_user_status ON fines (user_id, status);
