package com.library.fine.application.usecase;

import com.library.fine.application.dto.FineResponse;
import com.library.fine.application.dto.PayFineCommand;
import com.library.fine.application.port.FineEventPublisher;
import com.library.fine.domain.event.FinePaidEvent;
import com.library.fine.domain.exception.FineNotFoundException;
import com.library.fine.domain.model.Fine;
import com.library.fine.domain.model.FineId;
import com.library.fine.domain.repository.FineRepository;

import java.time.Instant;

public class PayFineUseCase {

    private final FineRepository fineRepository;
    private final FineEventPublisher eventPublisher;

    public PayFineUseCase(FineRepository fineRepository, FineEventPublisher eventPublisher) {
        this.fineRepository = fineRepository;
        this.eventPublisher = eventPublisher;
    }

    public FineResponse execute(PayFineCommand command) {
        FineId fineId = new FineId(command.fineId());
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new FineNotFoundException(fineId));

        fine.pay(Instant.now());
        fineRepository.save(fine);
        eventPublisher.publish(FinePaidEvent.from(fine));

        return FineResponse.from(fine);
    }
}
