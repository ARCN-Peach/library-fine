package com.library.fine.infrastructure.persistence.mapper;

import com.library.fine.domain.model.Fine;
import com.library.fine.domain.model.FineId;
import com.library.fine.domain.model.Money;
import com.library.fine.domain.model.RentalId;
import com.library.fine.domain.model.UserId;
import com.library.fine.infrastructure.persistence.FineJpaEntity;

public class FineMapper {

    private FineMapper() {}

    public static Fine toDomain(FineJpaEntity entity) {
        return Fine.reconstitute(
                new FineId(entity.getFineId()),
                new RentalId(entity.getRentalId()),
                new UserId(entity.getUserId()),
                Money.of(entity.getAmount(), entity.getCurrency()),
                entity.getStatus(),
                entity.getGeneratedAt(),
                entity.getPaidAt()
        );
    }

    public static FineJpaEntity toJpa(Fine fine) {
        return new FineJpaEntity(
                fine.getFineId().getValue(),
                fine.getRentalId().getValue(),
                fine.getUserId().getValue(),
                fine.getAmount().getAmount(),
                fine.getAmount().getCurrency(),
                fine.getStatus(),
                fine.getGeneratedAt(),
                fine.getPaidAt()
        );
    }
}
