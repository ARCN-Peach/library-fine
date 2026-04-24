package com.library.fine.infrastructure.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxPublisherJob {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisherJob.class);

    private final OutboxJpaRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;

    public OutboxPublisherJob(OutboxJpaRepository outboxRepository, RabbitTemplate rabbitTemplate) {
        this.outboxRepository = outboxRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelayString = "${library.fine.outbox.poll-interval-ms:5000}")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxJpaEntity> pending = outboxRepository.findByPublishedFalseOrderByOccurredAtAsc();
        for (OutboxJpaEntity event : pending) {
            try {
                rabbitTemplate.convertAndSend(event.getExchange(), event.getRoutingKey(), event.getPayload());
                event.markPublished();
                outboxRepository.save(event);
                log.info("Published outbox event: type={} eventId={}", event.getEventType(), event.getEventId());
            } catch (Exception ex) {
                log.error("Failed to publish outbox event: eventId={}", event.getEventId(), ex);
            }
        }
    }
}
