package com.library.fine.domain.event;

import com.library.fine.domain.model.Fine;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FineGeneratedEvent(
        UUID eventId,
        String eventType,
        String version,
        Instant occurredAt,
        UUID correlationId,
        UUID fineId,
        UUID rentalId,
        UUID userId,
        BigDecimal amount,
        String currency
) {
    private static final String EVENT_TYPE = "fine.fine.fine_generated.v1";
    private static final String VERSION = "1";

    public static FineGeneratedEvent from(Fine fine) {
        return new FineGeneratedEvent(
                UUID.randomUUID(),
                EVENT_TYPE,
                VERSION,
                Instant.now(),
                UUID.randomUUID(),
                fine.getFineId().getValue(),
                fine.getRentalId().getValue(),
                fine.getUserId().getValue(),
                fine.getAmount().getAmount(),
                fine.getAmount().getCurrency()
        );
    }
}
