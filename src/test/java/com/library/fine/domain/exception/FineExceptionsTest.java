package com.library.fine.domain.exception;

import com.library.fine.domain.model.FineId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FineExceptionsTest {

    @Test
    void fine_not_found_exception_contains_fine_id() {
        FineId fineId = new FineId(UUID.randomUUID());

        FineNotFoundException ex = new FineNotFoundException(fineId);

        assertThat(ex.getMessage()).contains(fineId.getValue().toString());
    }

    @Test
    void fine_already_paid_exception_contains_fine_id() {
        FineId fineId = new FineId(UUID.randomUUID());

        FineAlreadyPaidException ex = new FineAlreadyPaidException(fineId);

        assertThat(ex.getMessage()).contains(fineId.getValue().toString());
    }
}
