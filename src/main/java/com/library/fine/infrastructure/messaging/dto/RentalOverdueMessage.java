package com.library.fine.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RentalOverdueMessage(
        UUID eventId,
        String correlationId,
        UUID rentalId,
        UUID bookId,
        UUID userId,
        long daysOverdue,
        Instant occurredAt
) {}
