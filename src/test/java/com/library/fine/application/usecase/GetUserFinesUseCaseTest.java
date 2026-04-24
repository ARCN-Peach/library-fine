package com.library.fine.application.usecase;

import com.library.fine.application.dto.FineResponse;
import com.library.fine.application.dto.GenerateFineCommand;
import com.library.fine.application.dto.PayFineCommand;
import com.library.fine.domain.service.FineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GetUserFinesUseCaseTest {

    private FineRepositoryInMemory repository;
    private GenerateFineUseCase generateFineUseCase;
    private PayFineUseCase payFineUseCase;
    private GetUserFinesUseCase getUserFinesUseCase;

    @BeforeEach
    void setUp() {
        repository = new FineRepositoryInMemory();
        GenerateFineUseCaseTest.CapturingEventPublisher eventPublisher =
                new GenerateFineUseCaseTest.CapturingEventPublisher();
        generateFineUseCase = new GenerateFineUseCase(repository, new FineService(), eventPublisher);
        payFineUseCase = new PayFineUseCase(repository, eventPublisher);
        getUserFinesUseCase = new GetUserFinesUseCase(repository);
    }

    @Test
    void returns_all_user_fines_when_only_pending_is_false() {
        UUID userId = UUID.randomUUID();

        FineResponse pendingFine = generateFineUseCase
                .execute(new GenerateFineCommand(UUID.randomUUID(), userId, 3))
                .orElseThrow();
        FineResponse fineToPay = generateFineUseCase
                .execute(new GenerateFineCommand(UUID.randomUUID(), userId, 1))
                .orElseThrow();
        generateFineUseCase.execute(new GenerateFineCommand(UUID.randomUUID(), UUID.randomUUID(), 2));

        payFineUseCase.execute(new PayFineCommand(fineToPay.fineId()));

        List<FineResponse> result = getUserFinesUseCase.execute(userId, false);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(FineResponse::fineId)
                .containsExactlyInAnyOrder(pendingFine.fineId(), fineToPay.fineId());
    }

    @Test
    void returns_only_pending_user_fines_when_flag_is_true() {
        UUID userId = UUID.randomUUID();

        FineResponse pendingFine = generateFineUseCase
                .execute(new GenerateFineCommand(UUID.randomUUID(), userId, 2))
                .orElseThrow();
        FineResponse fineToPay = generateFineUseCase
                .execute(new GenerateFineCommand(UUID.randomUUID(), userId, 4))
                .orElseThrow();

        payFineUseCase.execute(new PayFineCommand(fineToPay.fineId()));

        List<FineResponse> result = getUserFinesUseCase.execute(userId, true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).fineId()).isEqualTo(pendingFine.fineId());
    }
}
