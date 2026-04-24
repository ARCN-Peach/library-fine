package com.library.fine.domain.service;

import com.library.fine.domain.model.Fine;
import com.library.fine.domain.model.FineId;
import com.library.fine.domain.model.Money;
import com.library.fine.domain.model.RentalId;
import com.library.fine.domain.model.UserId;

import java.math.BigDecimal;
import java.time.Instant;

public class FineService {

    private static final BigDecimal RATE_PER_DAY = new BigDecimal("1.00");
    private static final String CURRENCY = "USD";

    public Fine generateFine(RentalId rentalId, UserId userId, long daysOverdue, Instant now) {
        if (daysOverdue <= 0) {
            throw new IllegalArgumentException("daysOverdue must be positive to generate a fine");
        }
        Money amount = Money.of(RATE_PER_DAY.multiply(BigDecimal.valueOf(daysOverdue)), CURRENCY);
        return Fine.generate(FineId.generate(), rentalId, userId, amount, now);
    }
}
