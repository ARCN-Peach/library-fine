package com.library.fine.infrastructure.persistence;

import com.library.fine.domain.model.FineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FineJpaRepository extends JpaRepository<FineJpaEntity, UUID> {

    List<FineJpaEntity> findByUserIdAndStatus(UUID userId, FineStatus status);

    List<FineJpaEntity> findByUserId(UUID userId);

    boolean existsByRentalId(UUID rentalId);
}
