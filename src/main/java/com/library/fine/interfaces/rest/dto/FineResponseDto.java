package com.library.fine.interfaces.rest.dto;

import com.library.fine.application.dto.FineResponse;
import com.library.fine.domain.model.FineStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FineResponseDto(
    @Schema(description = "Id de la multa", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID fineId,
    @Schema(description = "Id del prestamo asociado", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
        UUID rentalId,
    @Schema(description = "Id del usuario", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID userId,
    @Schema(description = "Monto de la multa", example = "5.00")
        BigDecimal amount,
    @Schema(description = "Moneda", example = "USD")
        String currency,
    @Schema(description = "Estado de la multa", example = "PENDING")
        FineStatus status,
    @Schema(description = "Fecha de generacion", example = "2026-04-25T15:30:00Z")
        Instant generatedAt,
    @Schema(description = "Fecha de pago; null si sigue pendiente", example = "2026-04-26T10:00:00Z", nullable = true)
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
