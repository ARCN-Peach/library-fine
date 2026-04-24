package com.library.fine.domain.model;

import com.library.fine.domain.exception.FineAlreadyPaidException;

import java.time.Instant;
import java.util.Objects;

public class Fine {

    private final FineId fineId;
    private final RentalId rentalId;
    private final UserId userId;
    private final Money amount;
    private FineStatus status;
    private final Instant generatedAt;
    private Instant paidAt;

    private Fine(FineId fineId, RentalId rentalId, UserId userId, Money amount,
                 FineStatus status, Instant generatedAt, Instant paidAt) {
        this.fineId = Objects.requireNonNull(fineId, "fineId must not be null");
        this.rentalId = Objects.requireNonNull(rentalId, "rentalId must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.generatedAt = Objects.requireNonNull(generatedAt, "generatedAt must not be null");
        this.paidAt = paidAt;
    }

    public static Fine generate(FineId fineId, RentalId rentalId, UserId userId,
                                Money amount, Instant now) {
        return new Fine(fineId, rentalId, userId, amount, FineStatus.PENDING, now, null);
    }

    public static Fine reconstitute(FineId fineId, RentalId rentalId, UserId userId,
                                    Money amount, FineStatus status,
                                    Instant generatedAt, Instant paidAt) {
        return new Fine(fineId, rentalId, userId, amount, status, generatedAt, paidAt);
    }

    public void pay(Instant now) {
        if (this.status != FineStatus.PENDING) {
            throw new FineAlreadyPaidException(this.fineId);
        }
        this.status = FineStatus.PAID;
        this.paidAt = Objects.requireNonNull(now, "paidAt must not be null");
    }

    public boolean isPending() {
        return this.status == FineStatus.PENDING;
    }

    public FineId getFineId() { return fineId; }
    public RentalId getRentalId() { return rentalId; }
    public UserId getUserId() { return userId; }
    public Money getAmount() { return amount; }
    public FineStatus getStatus() { return status; }
    public Instant getGeneratedAt() { return generatedAt; }
    public Instant getPaidAt() { return paidAt; }
}
