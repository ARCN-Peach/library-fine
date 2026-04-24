package com.library.fine.domain.event;

import com.library.fine.domain.model.Fine;

import java.time.Instant;
import java.util.UUID;

public record FinePaidEvent(
        UUID eventId,
        String eventType,
        String version,
        Instant occurredAt,
        UUID correlationId,
        UUID fineId,
        UUID userId
) {
    private static final String EVENT_TYPE = "fine.fine.fine_paid.v1";
    private static final String VERSION = "1";

    public static FinePaidEvent from(Fine fine) {
        return new FinePaidEvent(
                UUID.randomUUID(),
                EVENT_TYPE,
                VERSION,
                Instant.now(),
                UUID.randomUUID(),
                fine.getFineId().getValue(),
                fine.getUserId().getValue()
        );
    }
}
