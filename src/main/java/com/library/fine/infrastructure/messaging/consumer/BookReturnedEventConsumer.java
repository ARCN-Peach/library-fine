package com.library.fine.infrastructure.messaging.consumer;

import com.library.fine.application.dto.GenerateFineCommand;
import com.library.fine.application.usecase.GenerateFineUseCase;
import com.library.fine.infrastructure.config.RabbitMQConfig;
import com.library.fine.infrastructure.messaging.dto.BookReturnedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Component
public class BookReturnedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(BookReturnedEventConsumer.class);

    private final GenerateFineUseCase generateFineUseCase;

    public BookReturnedEventConsumer(GenerateFineUseCase generateFineUseCase) {
        this.generateFineUseCase = generateFineUseCase;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_BOOK_RETURNED)
    @Transactional
    public void onBookReturned(BookReturnedMessage message) {
        log.info("Received BookReturnedEvent: rentalId={} correlationId={}",
                message.rentalId(), message.correlationId());

        if (message.dueDate() == null || message.returnDate() == null) {
            log.warn("BookReturnedEvent missing date fields, skipping: rentalId={}", message.rentalId());
            return;
        }

        long daysOverdue = Duration.between(message.dueDate(), message.returnDate()).toDays();

        generateFineUseCase.execute(new GenerateFineCommand(
                message.rentalId(),
                message.userId(),
                daysOverdue
        )).ifPresent(fine ->
                log.info("Fine generated from BookReturnedEvent: fineId={} userId={} amount={}",
                        fine.fineId(), fine.userId(), fine.amount())
        );
    }
}
