package com.library.fine.domain.model;

import com.library.fine.domain.exception.FineAlreadyPaidException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FineTest {

    private static final FineId FINE_ID = new FineId(UUID.randomUUID());
    private static final RentalId RENTAL_ID = new RentalId(UUID.randomUUID());
    private static final UserId USER_ID = new UserId(UUID.randomUUID());
    private static final Money AMOUNT = Money.of(new BigDecimal("3.00"), "USD");

    @Test
    void generates_fine_with_pending_status() {
        Fine fine = Fine.generate(FINE_ID, RENTAL_ID, USER_ID, AMOUNT, Instant.now());

        assertThat(fine.getStatus()).isEqualTo(FineStatus.PENDING);
        assertThat(fine.isPending()).isTrue();
        assertThat(fine.getPaidAt()).isNull();
    }

    @Test
    void pay_transitions_status_to_paid() {
        Fine fine = Fine.generate(FINE_ID, RENTAL_ID, USER_ID, AMOUNT, Instant.now());
        Instant paidAt = Instant.now();

        fine.pay(paidAt);

        assertThat(fine.getStatus()).isEqualTo(FineStatus.PAID);
        assertThat(fine.isPending()).isFalse();
        assertThat(fine.getPaidAt()).isEqualTo(paidAt);
    }

    @Test
    void pay_twice_throws_already_paid_exception() {
        Fine fine = Fine.generate(FINE_ID, RENTAL_ID, USER_ID, AMOUNT, Instant.now());
        fine.pay(Instant.now());

        assertThatThrownBy(() -> fine.pay(Instant.now()))
                .isInstanceOf(FineAlreadyPaidException.class);
    }

    @Test
    void reconstitute_preserves_all_fields() {
        Instant generatedAt = Instant.now().minusSeconds(3600);
        Instant paidAt = Instant.now();

        Fine fine = Fine.reconstitute(FINE_ID, RENTAL_ID, USER_ID, AMOUNT,
                FineStatus.PAID, generatedAt, paidAt);

        assertThat(fine.getFineId()).isEqualTo(FINE_ID);
        assertThat(fine.getRentalId()).isEqualTo(RENTAL_ID);
        assertThat(fine.getUserId()).isEqualTo(USER_ID);
        assertThat(fine.getAmount()).isEqualTo(AMOUNT);
        assertThat(fine.getStatus()).isEqualTo(FineStatus.PAID);
        assertThat(fine.getGeneratedAt()).isEqualTo(generatedAt);
        assertThat(fine.getPaidAt()).isEqualTo(paidAt);
    }

    @Test
    void generate_rejects_null_fineId() {
        assertThatThrownBy(() -> Fine.generate(null, RENTAL_ID, USER_ID, AMOUNT, Instant.now()))
                .isInstanceOf(NullPointerException.class);
    }
}
