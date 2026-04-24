package com.library.fine.infrastructure.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxJpaEntity {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Column(name = "event_type", nullable = false, updatable = false)
    private String eventType;

    @Column(name = "routing_key", nullable = false, updatable = false)
    private String routingKey;

    @Column(name = "exchange", nullable = false, updatable = false)
    private String exchange;

    @Column(name = "payload", nullable = false, updatable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Column(name = "published_at")
    private Instant publishedAt;

    protected OutboxJpaEntity() {}

    public OutboxJpaEntity(UUID eventId, String eventType, String routingKey,
                           String exchange, String payload, Instant occurredAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.routingKey = routingKey;
        this.exchange = exchange;
        this.payload = payload;
        this.occurredAt = occurredAt;
        this.published = false;
    }

    public void markPublished() {
        this.published = true;
        this.publishedAt = Instant.now();
    }

    public UUID getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public String getRoutingKey() { return routingKey; }
    public String getExchange() { return exchange; }
    public String getPayload() { return payload; }
    public Instant getOccurredAt() { return occurredAt; }
    public boolean isPublished() { return published; }
    public Instant getPublishedAt() { return publishedAt; }
}
