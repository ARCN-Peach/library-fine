package com.library.fine.infrastructure.config;

import com.library.fine.application.port.FineEventPublisher;
import com.library.fine.application.usecase.GenerateFineUseCase;
import com.library.fine.application.usecase.GetFineUseCase;
import com.library.fine.application.usecase.GetUserFinesUseCase;
import com.library.fine.application.usecase.PayFineUseCase;
import com.library.fine.domain.repository.FineRepository;
import com.library.fine.domain.service.FineService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public FineService fineService() {
        return new FineService();
    }

    @Bean
    public GenerateFineUseCase generateFineUseCase(FineRepository fineRepository,
                                                   FineService fineService,
                                                   FineEventPublisher eventPublisher) {
        return new GenerateFineUseCase(fineRepository, fineService, eventPublisher);
    }

    @Bean
    public PayFineUseCase payFineUseCase(FineRepository fineRepository,
                                         FineEventPublisher eventPublisher) {
        return new PayFineUseCase(fineRepository, eventPublisher);
    }

    @Bean
    public GetFineUseCase getFineUseCase(FineRepository fineRepository) {
        return new GetFineUseCase(fineRepository);
    }

    @Bean
    public GetUserFinesUseCase getUserFinesUseCase(FineRepository fineRepository) {
        return new GetUserFinesUseCase(fineRepository);
    }
}
