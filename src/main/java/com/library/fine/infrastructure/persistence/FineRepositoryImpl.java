package com.library.fine.infrastructure.persistence;

import com.library.fine.domain.model.Fine;
import com.library.fine.domain.model.FineId;
import com.library.fine.domain.model.FineStatus;
import com.library.fine.domain.model.RentalId;
import com.library.fine.domain.model.UserId;
import com.library.fine.domain.repository.FineRepository;
import com.library.fine.infrastructure.persistence.mapper.FineMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FineRepositoryImpl implements FineRepository {

    private final FineJpaRepository jpaRepository;

    public FineRepositoryImpl(FineJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Fine> findById(FineId fineId) {
        return jpaRepository.findById(fineId.getValue()).map(FineMapper::toDomain);
    }

    @Override
    public List<Fine> findPendingByUser(UserId userId) {
        return jpaRepository.findByUserIdAndStatus(userId.getValue(), FineStatus.PENDING)
                .stream().map(FineMapper::toDomain).toList();
    }

    @Override
    public List<Fine> findByUser(UserId userId) {
        return jpaRepository.findByUserId(userId.getValue())
                .stream().map(FineMapper::toDomain).toList();
    }

    @Override
    public boolean existsByRentalId(RentalId rentalId) {
        return jpaRepository.existsByRentalId(rentalId.getValue());
    }

    @Override
    public Fine save(Fine fine) {
        FineJpaEntity saved = jpaRepository.save(FineMapper.toJpa(fine));
        return FineMapper.toDomain(saved);
    }
}
