CREATE TABLE outbox_events (
    event_id     UUID         NOT NULL,
    event_type   VARCHAR(100) NOT NULL,
    routing_key  VARCHAR(200) NOT NULL,
    exchange     VARCHAR(100) NOT NULL,
    payload      TEXT         NOT NULL,
    occurred_at  TIMESTAMPTZ  NOT NULL,
    published    BOOLEAN      NOT NULL DEFAULT FALSE,
    published_at TIMESTAMPTZ,

    CONSTRAINT pk_outbox_events PRIMARY KEY (event_id)
);

CREATE INDEX idx_outbox_unpublished ON outbox_events (published, occurred_at)
    WHERE published = FALSE;
