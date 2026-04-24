package com.library.fine.application.usecase;

import com.library.fine.application.dto.FineResponse;
import com.library.fine.domain.model.UserId;
import com.library.fine.domain.repository.FineRepository;

import java.util.List;
import java.util.UUID;

public class GetUserFinesUseCase {

    private final FineRepository fineRepository;

    public GetUserFinesUseCase(FineRepository fineRepository) {
        this.fineRepository = fineRepository;
    }

    public List<FineResponse> execute(UUID userId, boolean onlyPending) {
        UserId uid = new UserId(userId);
        if (onlyPending) {
            return fineRepository.findPendingByUser(uid)
                    .stream().map(FineResponse::from).toList();
        }
        return fineRepository.findByUser(uid)
                .stream().map(FineResponse::from).toList();
    }
}
