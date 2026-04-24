package com.library.fine.application.usecase;

import com.library.fine.application.dto.FineResponse;
import com.library.fine.application.dto.GenerateFineCommand;
import com.library.fine.domain.exception.FineNotFoundException;
import com.library.fine.domain.service.FineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetFineUseCaseTest {

    private FineRepositoryInMemory repository;
    private GetFineUseCase getFineUseCase;
    private GenerateFineUseCase generateFineUseCase;

    @BeforeEach
    void setUp() {
        repository = new FineRepositoryInMemory();
        getFineUseCase = new GetFineUseCase(repository);
        generateFineUseCase = new GenerateFineUseCase(
                repository,
                new FineService(),
                new GenerateFineUseCaseTest.CapturingEventPublisher()
        );
    }

    @Test
    void returns_fine_when_it_exists() {
        FineResponse generated = generateFineUseCase
                .execute(new GenerateFineCommand(UUID.randomUUID(), UUID.randomUUID(), 2))
                .orElseThrow();

        FineResponse result = getFineUseCase.execute(generated.fineId());

        assertThat(result.fineId()).isEqualTo(generated.fineId());
        assertThat(result.userId()).isEqualTo(generated.userId());
    }

    @Test
    void throws_not_found_when_fine_does_not_exist() {
        assertThatThrownBy(() -> getFineUseCase.execute(UUID.randomUUID()))
                .isInstanceOf(FineNotFoundException.class)
                .hasMessageContaining("Fine not found");
    }
}
