package com.library.fine.application.usecase;

import com.library.fine.application.dto.FineResponse;
import com.library.fine.application.dto.GenerateFineCommand;
import com.library.fine.application.dto.PayFineCommand;
import com.library.fine.application.port.FineEventPublisher;
import com.library.fine.domain.event.FineGeneratedEvent;
import com.library.fine.domain.event.FinePaidEvent;
import com.library.fine.domain.exception.FineAlreadyPaidException;
import com.library.fine.domain.exception.FineNotFoundException;
import com.library.fine.domain.model.FineStatus;
import com.library.fine.domain.service.FineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PayFineUseCaseTest {

    private FineRepositoryInMemory repository;
    private CapturingEventPublisher eventPublisher;
    private GenerateFineUseCase generateFineUseCase;
    private PayFineUseCase payFineUseCase;

    @BeforeEach
    void setUp() {
        repository = new FineRepositoryInMemory();
        eventPublisher = new CapturingEventPublisher();
        generateFineUseCase = new GenerateFineUseCase(repository, new FineService(), eventPublisher);
        payFineUseCase = new PayFineUseCase(repository, eventPublisher);
    }

    @Test
    void pays_pending_fine_and_changes_status_to_paid() {
        Optional<FineResponse> generated = generateFineUseCase.execute(
                new GenerateFineCommand(UUID.randomUUID(), UUID.randomUUID(), 2));
        UUID fineId = generated.get().fineId();

        FineResponse paid = payFineUseCase.execute(new PayFineCommand(fineId));

        assertThat(paid.status()).isEqualTo(FineStatus.PAID);
        assertThat(paid.paidAt()).isNotNull();
    }

    @Test
    void publishes_fine_paid_event() {
        Optional<FineResponse> generated = generateFineUseCase.execute(
                new GenerateFineCommand(UUID.randomUUID(), UUID.randomUUID(), 1));

        payFineUseCase.execute(new PayFineCommand(generated.get().fineId()));

        assertThat(eventPublisher.paidEvents).hasSize(1);
        assertThat(eventPublisher.paidEvents.get(0).userId()).isEqualTo(generated.get().userId());
    }

    @Test
    void throws_not_found_for_unknown_fine() {
        assertThatThrownBy(() -> payFineUseCase.execute(new PayFineCommand(UUID.randomUUID())))
                .isInstanceOf(FineNotFoundException.class);
    }

    @Test
    void throws_already_paid_when_paying_twice() {
        Optional<FineResponse> generated = generateFineUseCase.execute(
                new GenerateFineCommand(UUID.randomUUID(), UUID.randomUUID(), 3));
        UUID fineId = generated.get().fineId();

        payFineUseCase.execute(new PayFineCommand(fineId));

        assertThatThrownBy(() -> payFineUseCase.execute(new PayFineCommand(fineId)))
                .isInstanceOf(FineAlreadyPaidException.class);
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
