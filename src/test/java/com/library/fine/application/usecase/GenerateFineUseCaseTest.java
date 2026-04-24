package com.library.fine.application.usecase;

import com.library.fine.application.dto.FineResponse;
import com.library.fine.application.dto.GenerateFineCommand;
import com.library.fine.application.port.FineEventPublisher;
import com.library.fine.domain.event.FineGeneratedEvent;
import com.library.fine.domain.event.FinePaidEvent;
import com.library.fine.domain.model.FineStatus;
import com.library.fine.domain.service.FineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GenerateFineUseCaseTest {

    private FineRepositoryInMemory repository;
    private CapturingEventPublisher eventPublisher;
    private GenerateFineUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new FineRepositoryInMemory();
        eventPublisher = new CapturingEventPublisher();
        useCase = new GenerateFineUseCase(repository, new FineService(), eventPublisher);
    }

    @Test
    void generates_fine_when_rental_is_overdue() {
        UUID rentalId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Optional<FineResponse> result = useCase.execute(new GenerateFineCommand(rentalId, userId, 5));

        assertThat(result).isPresent();
        FineResponse fine = result.get();
        assertThat(fine.status()).isEqualTo(FineStatus.PENDING);
        assertThat(fine.amount()).isEqualByComparingTo(new BigDecimal("5.00"));
        assertThat(fine.userId()).isEqualTo(userId);
    }

    @Test
    void publishes_fine_generated_event() {
        useCase.execute(new GenerateFineCommand(UUID.randomUUID(), UUID.randomUUID(), 2));

        assertThat(eventPublisher.generatedEvents).hasSize(1);
    }

    @Test
    void returns_empty_when_days_overdue_is_zero() {
        Optional<FineResponse> result = useCase.execute(
                new GenerateFineCommand(UUID.randomUUID(), UUID.randomUUID(), 0));

        assertThat(result).isEmpty();
        assertThat(eventPublisher.generatedEvents).isEmpty();
    }

    @Test
    void is_idempotent_for_same_rental() {
        UUID rentalId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        useCase.execute(new GenerateFineCommand(rentalId, userId, 3));
        Optional<FineResponse> second = useCase.execute(new GenerateFineCommand(rentalId, userId, 3));

        assertThat(second).isEmpty();
        assertThat(eventPublisher.generatedEvents).hasSize(1);
    }

    @Test
    void does_not_generate_fine_when_not_overdue() {
        Optional<FineResponse> result = useCase.execute(
                new GenerateFineCommand(UUID.randomUUID(), UUID.randomUUID(), -1));

        assertThat(result).isEmpty();
    }

    static class CapturingEventPublisher implements FineEventPublisher {
        final List<FineGeneratedEvent> generatedEvents = new ArrayList<>();
        final List<FinePaidEvent> paidEvents = new ArrayList<>();

        @Override
        public void publish(FineGeneratedEvent event) { generatedEvents.add(event); }

        @Override
        public void publish(FinePaidEvent event) { paidEvents.add(event); }
    }
}
