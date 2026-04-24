package com.library.fine.application.port;

import com.library.fine.domain.event.FineGeneratedEvent;
import com.library.fine.domain.event.FinePaidEvent;

public interface FineEventPublisher {

    void publish(FineGeneratedEvent event);

    void publish(FinePaidEvent event);
}
