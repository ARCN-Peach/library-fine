package com.library.fine.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxJpaRepository extends JpaRepository<OutboxJpaEntity, UUID> {

    List<OutboxJpaEntity> findByPublishedFalseOrderByOccurredAtAsc();
}
