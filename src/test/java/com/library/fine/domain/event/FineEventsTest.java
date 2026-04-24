package com.library.fine.domain.event;

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

class FineEventsTest {

    @Test
    void fine_generated_event_maps_fine_data_and_metadata() {
        Fine fine = Fine.generate(
                new FineId(UUID.randomUUID()),
                new RentalId(UUID.randomUUID()),
                new UserId(UUID.randomUUID()),
                Money.of(new BigDecimal("7.50"), "USD"),
                Instant.now()
        );

        FineGeneratedEvent event = FineGeneratedEvent.from(fine);

        assertThat(event.eventType()).isEqualTo("fine.fine.fine_generated.v1");
        assertThat(event.version()).isEqualTo("1");
        assertThat(event.eventId()).isNotNull();
        assertThat(event.correlationId()).isNotNull();
        assertThat(event.occurredAt()).isNotNull();
        assertThat(event.fineId()).isEqualTo(fine.getFineId().getValue());
        assertThat(event.rentalId()).isEqualTo(fine.getRentalId().getValue());
        assertThat(event.userId()).isEqualTo(fine.getUserId().getValue());
        assertThat(event.amount()).isEqualByComparingTo("7.50");
        assertThat(event.currency()).isEqualTo("USD");
    }

    @Test
    void fine_paid_event_maps_fine_data_and_metadata() {
        Fine fine = Fine.generate(
                new FineId(UUID.randomUUID()),
                new RentalId(UUID.randomUUID()),
                new UserId(UUID.randomUUID()),
                Money.of(new BigDecimal("1.00"), "USD"),
                Instant.now()
        );

        FinePaidEvent event = FinePaidEvent.from(fine);

        assertThat(event.eventType()).isEqualTo("fine.fine.fine_paid.v1");
        assertThat(event.version()).isEqualTo("1");
        assertThat(event.eventId()).isNotNull();
        assertThat(event.correlationId()).isNotNull();
        assertThat(event.occurredAt()).isNotNull();
        assertThat(event.fineId()).isEqualTo(fine.getFineId().getValue());
        assertThat(event.userId()).isEqualTo(fine.getUserId().getValue());
    }
}
