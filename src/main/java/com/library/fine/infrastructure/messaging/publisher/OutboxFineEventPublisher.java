package com.library.fine.infrastructure.messaging.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.fine.application.port.FineEventPublisher;
import com.library.fine.domain.event.FineGeneratedEvent;
import com.library.fine.domain.event.FinePaidEvent;
import com.library.fine.infrastructure.config.RabbitMQConfig;
import com.library.fine.infrastructure.outbox.OutboxJpaEntity;
import com.library.fine.infrastructure.outbox.OutboxJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class OutboxFineEventPublisher implements FineEventPublisher {

    private final OutboxJpaRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxFineEventPublisher(OutboxJpaRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(FineGeneratedEvent event) {
        outboxRepository.save(new OutboxJpaEntity(
                event.eventId(),
                event.eventType(),
                RabbitMQConfig.ROUTING_KEY_FINE_GENERATED,
                RabbitMQConfig.EXCHANGE_FINE,
                serialize(event),
                event.occurredAt()
        ));
    }

    @Override
    public void publish(FinePaidEvent event) {
        outboxRepository.save(new OutboxJpaEntity(
                event.eventId(),
                event.eventType(),
                RabbitMQConfig.ROUTING_KEY_FINE_PAID,
                RabbitMQConfig.EXCHANGE_FINE,
                serialize(event),
                event.occurredAt()
        ));
    }

    private String serialize(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize event: " + event.getClass().getSimpleName(), e);
        }
    }
}
