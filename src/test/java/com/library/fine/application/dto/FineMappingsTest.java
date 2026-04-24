package com.library.fine.application.dto;

import com.library.fine.interfaces.rest.dto.FineResponseDto;
import com.library.fine.interfaces.rest.dto.PayFineRequest;
import com.library.fine.domain.model.Fine;
import com.library.fine.domain.model.FineId;
import com.library.fine.domain.model.Money;
import com.library.fine.domain.model.RentalId;
import com.library.fine.domain.model.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FineMappingsTest {

    @Test
    void maps_domain_fine_to_application_and_rest_dtos() {
        Instant generatedAt = Instant.now();
        Fine fine = Fine.generate(
                new FineId(UUID.randomUUID()),
                new RentalId(UUID.randomUUID()),
                new UserId(UUID.randomUUID()),
                Money.of(new BigDecimal("2.00"), "USD"),
                generatedAt
        );

        FineResponse appResponse = FineResponse.from(fine);
        FineResponseDto restResponse = FineResponseDto.from(appResponse);

        assertThat(appResponse.fineId()).isEqualTo(fine.getFineId().getValue());
        assertThat(appResponse.rentalId()).isEqualTo(fine.getRentalId().getValue());
        assertThat(appResponse.userId()).isEqualTo(fine.getUserId().getValue());
        assertThat(appResponse.amount()).isEqualByComparingTo("2.00");
        assertThat(appResponse.currency()).isEqualTo("USD");
        assertThat(appResponse.generatedAt()).isEqualTo(generatedAt);

        assertThat(restResponse.fineId()).isEqualTo(appResponse.fineId());
        assertThat(restResponse.userId()).isEqualTo(appResponse.userId());
        assertThat(restResponse.amount()).isEqualByComparingTo("2.00");
    }

    @Test
    void record_commands_and_request_keep_values() {
        UUID rentalId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID fineId = UUID.randomUUID();

        GenerateFineCommand generateFineCommand = new GenerateFineCommand(rentalId, userId, 5);
        PayFineCommand payFineCommand = new PayFineCommand(fineId);
        PayFineRequest payFineRequest = new PayFineRequest(fineId);

        assertThat(generateFineCommand.rentalId()).isEqualTo(rentalId);
        assertThat(generateFineCommand.userId()).isEqualTo(userId);
        assertThat(generateFineCommand.daysOverdue()).isEqualTo(5);
        assertThat(payFineCommand.fineId()).isEqualTo(fineId);
        assertThat(payFineRequest.fineId()).isEqualTo(fineId);
    }
}
