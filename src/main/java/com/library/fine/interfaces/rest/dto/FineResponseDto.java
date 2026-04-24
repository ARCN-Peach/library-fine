package com.library.fine.interfaces.rest.dto;

import com.library.fine.application.dto.FineResponse;
import com.library.fine.domain.model.FineStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FineResponseDto(
        UUID fineId,
        UUID rentalId,
        UUID userId,
        BigDecimal amount,
        String currency,
        FineStatus status,
        Instant generatedAt,
        Instant paidAt
) {
    public static FineResponseDto from(FineResponse response) {
        return new FineResponseDto(
                response.fineId(),
                response.rentalId(),
                response.userId(),
                response.amount(),
                response.currency(),
                response.status(),
                response.generatedAt(),
                response.paidAt()
        );
    }
}
