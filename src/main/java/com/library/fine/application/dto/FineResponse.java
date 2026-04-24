package com.library.fine.application.dto;

import com.library.fine.domain.model.Fine;
import com.library.fine.domain.model.FineStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FineResponse(
        UUID fineId,
        UUID rentalId,
        UUID userId,
        BigDecimal amount,
        String currency,
        FineStatus status,
        Instant generatedAt,
        Instant paidAt
) {
    public static FineResponse from(Fine fine) {
        return new FineResponse(
                fine.getFineId().getValue(),
                fine.getRentalId().getValue(),
                fine.getUserId().getValue(),
                fine.getAmount().getAmount(),
                fine.getAmount().getCurrency(),
                fine.getStatus(),
                fine.getGeneratedAt(),
                fine.getPaidAt()
        );
    }
}
