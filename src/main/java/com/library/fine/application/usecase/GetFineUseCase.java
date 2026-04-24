package com.library.fine.application.usecase;

import com.library.fine.application.dto.FineResponse;
import com.library.fine.domain.exception.FineNotFoundException;
import com.library.fine.domain.model.FineId;
import com.library.fine.domain.repository.FineRepository;

import java.util.UUID;

public class GetFineUseCase {

    private final FineRepository fineRepository;

    public GetFineUseCase(FineRepository fineRepository) {
        this.fineRepository = fineRepository;
    }

    public FineResponse execute(UUID fineId) {
        FineId id = new FineId(fineId);
        return fineRepository.findById(id)
                .map(FineResponse::from)
                .orElseThrow(() -> new FineNotFoundException(id));
    }
}
