package com.library.fine.domain.repository;

import com.library.fine.domain.model.Fine;
import com.library.fine.domain.model.FineId;
import com.library.fine.domain.model.RentalId;
import com.library.fine.domain.model.UserId;

import java.util.List;
import java.util.Optional;

public interface FineRepository {

    Optional<Fine> findById(FineId fineId);

    List<Fine> findPendingByUser(UserId userId);

    List<Fine> findByUser(UserId userId);

    boolean existsByRentalId(RentalId rentalId);

    Fine save(Fine fine);
}
