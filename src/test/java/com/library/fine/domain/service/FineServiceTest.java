package com.library.fine.domain.service;

import com.library.fine.domain.model.Fine;
import com.library.fine.domain.model.FineStatus;
import com.library.fine.domain.model.RentalId;
import com.library.fine.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FineServiceTest {

    private FineService fineService;
    private final RentalId rentalId = new RentalId(UUID.randomUUID());
    private final UserId userId = new UserId(UUID.randomUUID());

    @BeforeEach
    void setUp() {
        fineService = new FineService();
    }

    @Test
    void generates_fine_proportional_to_days_overdue() {
        Fine fine = fineService.generateFine(rentalId, userId, 3, Instant.now());

        assertThat(fine.getAmount().getAmount()).isEqualByComparingTo(new BigDecimal("3.00"));
        assertThat(fine.getAmount().getCurrency()).isEqualTo("USD");
    }

    @Test
    void generated_fine_has_pending_status() {
        Fine fine = fineService.generateFine(rentalId, userId, 1, Instant.now());

        assertThat(fine.getStatus()).isEqualTo(FineStatus.PENDING);
    }

    @Test
    void generated_fine_has_correct_rental_and_user() {
        Fine fine = fineService.generateFine(rentalId, userId, 2, Instant.now());

        assertThat(fine.getRentalId()).isEqualTo(rentalId);
        assertThat(fine.getUserId()).isEqualTo(userId);
    }

    @Test
    void rejects_zero_days_overdue() {
        assertThatThrownBy(() -> fineService.generateFine(rentalId, userId, 0, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("daysOverdue must be positive");
    }

    @Test
    void rejects_negative_days_overdue() {
        assertThatThrownBy(() -> fineService.generateFine(rentalId, userId, -1, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void generates_unique_fine_id_each_call() {
        Fine first = fineService.generateFine(rentalId, userId, 1, Instant.now());
        Fine second = fineService.generateFine(new RentalId(UUID.randomUUID()), userId, 1, Instant.now());

        assertThat(first.getFineId()).isNotEqualTo(second.getFineId());
    }
}
