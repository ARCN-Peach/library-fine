package com.library.fine.application.usecase;

import com.library.fine.application.dto.FineResponse;
import com.library.fine.application.dto.GenerateFineCommand;
import com.library.fine.application.port.FineEventPublisher;
import com.library.fine.domain.event.FineGeneratedEvent;
import com.library.fine.domain.model.Fine;
import com.library.fine.domain.model.RentalId;
import com.library.fine.domain.model.UserId;
import com.library.fine.domain.repository.FineRepository;
import com.library.fine.domain.service.FineService;

import java.time.Instant;
import java.util.Optional;

public class GenerateFineUseCase {

    private final FineRepository fineRepository;
    private final FineService fineService;
    private final FineEventPublisher eventPublisher;

    public GenerateFineUseCase(FineRepository fineRepository, FineService fineService,
                               FineEventPublisher eventPublisher) {
        this.fineRepository = fineRepository;
        this.fineService = fineService;
        this.eventPublisher = eventPublisher;
    }

    public Optional<FineResponse> execute(GenerateFineCommand command) {
        RentalId rentalId = new RentalId(command.rentalId());

        if (fineRepository.existsByRentalId(rentalId)) {
            return Optional.empty();
        }

        if (command.daysOverdue() <= 0) {
            return Optional.empty();
        }

        Fine fine = fineService.generateFine(
                rentalId,
                new UserId(command.userId()),
                command.daysOverdue(),
                Instant.now()
        );

        fineRepository.save(fine);
        eventPublisher.publish(FineGeneratedEvent.from(fine));

        return Optional.of(FineResponse.from(fine));
    }
}
