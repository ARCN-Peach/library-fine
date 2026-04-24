package com.library.fine.infrastructure.messaging.consumer;

import com.library.fine.application.dto.GenerateFineCommand;
import com.library.fine.application.usecase.GenerateFineUseCase;
import com.library.fine.infrastructure.config.RabbitMQConfig;
import com.library.fine.infrastructure.messaging.dto.RentalOverdueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RentalOverdueEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(RentalOverdueEventConsumer.class);

    private final GenerateFineUseCase generateFineUseCase;

    public RentalOverdueEventConsumer(GenerateFineUseCase generateFineUseCase) {
        this.generateFineUseCase = generateFineUseCase;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_RENTAL_OVERDUE)
    @Transactional
    public void onRentalOverdue(RentalOverdueMessage message) {
        log.info("Received RentalOverdueEvent: rentalId={} daysOverdue={} correlationId={}",
                message.rentalId(), message.daysOverdue(), message.correlationId());

        generateFineUseCase.execute(new GenerateFineCommand(
                message.rentalId(),
                message.userId(),
                message.daysOverdue()
        )).ifPresent(fine ->
                log.info("Fine generated from RentalOverdueEvent: fineId={} userId={} amount={}",
                        fine.fineId(), fine.userId(), fine.amount())
        );
    }
}
