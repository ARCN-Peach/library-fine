package com.library.fine.application.dto;

import java.util.UUID;

public record GenerateFineCommand(
        UUID rentalId,
        UUID userId,
        long daysOverdue
) {}
