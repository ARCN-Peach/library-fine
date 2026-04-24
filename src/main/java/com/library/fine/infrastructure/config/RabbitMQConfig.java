package com.library.fine.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchanges
    public static final String EXCHANGE_FINE = "fine";
    public static final String EXCHANGE_RENTAL = "rental";

    // Routing keys produced by library-fine
    public static final String ROUTING_KEY_FINE_GENERATED = "fine.fine.fine_generated.v1";
    public static final String ROUTING_KEY_FINE_PAID = "fine.fine.fine_paid.v1";

    // Routing keys consumed from library-rental
    public static final String ROUTING_KEY_BOOK_RETURNED = "rental.rental.book_returned.v1";
    public static final String ROUTING_KEY_RENTAL_OVERDUE = "rental.rental.rental_overdue.v1";

    // Queues consumed by library-fine
    public static final String QUEUE_BOOK_RETURNED = "fine.book-returned";
    public static final String QUEUE_RENTAL_OVERDUE = "fine.rental-overdue";

    // Dead-letter exchange
    private static final String EXCHANGE_DLX = "fine.dlx";
    private static final String QUEUE_DLQ_BOOK_RETURNED = "fine.book-returned.dlq";
    private static final String QUEUE_DLQ_RENTAL_OVERDUE = "fine.rental-overdue.dlq";

    @Bean
    public DirectExchange fineExchange() {
        return new DirectExchange(EXCHANGE_FINE, true, false);
    }

    @Bean
    public DirectExchange rentalExchange() {
        return new DirectExchange(EXCHANGE_RENTAL, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(EXCHANGE_DLX, true, false);
    }

    @Bean
    public Queue bookReturnedQueue() {
        return QueueBuilder.durable(QUEUE_BOOK_RETURNED)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", QUEUE_DLQ_BOOK_RETURNED)
                .build();
    }

    @Bean
    public Queue rentalOverdueQueue() {
        return QueueBuilder.durable(QUEUE_RENTAL_OVERDUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", QUEUE_DLQ_RENTAL_OVERDUE)
                .build();
    }

    @Bean
    public Queue deadLetterQueueBookReturned() {
        return QueueBuilder.durable(QUEUE_DLQ_BOOK_RETURNED).build();
    }

    @Bean
    public Queue deadLetterQueueRentalOverdue() {
        return QueueBuilder.durable(QUEUE_DLQ_RENTAL_OVERDUE).build();
    }

    @Bean
    public Binding bookReturnedBinding(Queue bookReturnedQueue, DirectExchange rentalExchange) {
        return BindingBuilder.bind(bookReturnedQueue)
                .to(rentalExchange)
                .with(ROUTING_KEY_BOOK_RETURNED);
    }

    @Bean
    public Binding rentalOverdueBinding(Queue rentalOverdueQueue, DirectExchange rentalExchange) {
        return BindingBuilder.bind(rentalOverdueQueue)
                .to(rentalExchange)
                .with(ROUTING_KEY_RENTAL_OVERDUE);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
