package com.library.fine.application.usecase;

import com.library.fine.domain.model.Fine;
import com.library.fine.domain.model.FineId;
import com.library.fine.domain.model.FineStatus;
import com.library.fine.domain.model.RentalId;
import com.library.fine.domain.model.UserId;
import com.library.fine.domain.repository.FineRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class FineRepositoryInMemory implements FineRepository {

    private final List<Fine> fines = new ArrayList<>();

    @Override
    public Optional<Fine> findById(FineId fineId) {
        return fines.stream()
                .filter(f -> f.getFineId().equals(fineId))
                .findFirst();
    }

    @Override
    public List<Fine> findPendingByUser(UserId userId) {
        return fines.stream()
                .filter(f -> f.getUserId().equals(userId) && f.getStatus() == FineStatus.PENDING)
                .toList();
    }

    @Override
    public List<Fine> findByUser(UserId userId) {
        return fines.stream()
                .filter(f -> f.getUserId().equals(userId))
                .toList();
    }

    @Override
    public boolean existsByRentalId(RentalId rentalId) {
        return fines.stream().anyMatch(f -> f.getRentalId().equals(rentalId));
    }

    @Override
    public Fine save(Fine fine) {
        fines.removeIf(f -> f.getFineId().equals(fine.getFineId()));
        fines.add(fine);
        return fine;
    }
}
