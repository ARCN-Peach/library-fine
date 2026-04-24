package com.library.fine.infrastructure.persistence;

import com.library.fine.domain.model.FineStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fines")
public class FineJpaEntity {

    @Id
    @Column(name = "fine_id", nullable = false, updatable = false)
    private UUID fineId;

    @Column(name = "rental_id", nullable = false, updatable = false)
    private UUID rentalId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FineStatus status;

    @Column(name = "generated_at", nullable = false, updatable = false)
    private Instant generatedAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    protected FineJpaEntity() {}

    public FineJpaEntity(UUID fineId, UUID rentalId, UUID userId, BigDecimal amount,
                         String currency, FineStatus status, Instant generatedAt, Instant paidAt) {
        this.fineId = fineId;
        this.rentalId = rentalId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.generatedAt = generatedAt;
        this.paidAt = paidAt;
    }

    public UUID getFineId() { return fineId; }
    public UUID getRentalId() { return rentalId; }
    public UUID getUserId() { return userId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public FineStatus getStatus() { return status; }
    public Instant getGeneratedAt() { return generatedAt; }
    public Instant getPaidAt() { return paidAt; }

    public void setStatus(FineStatus status) { this.status = status; }
    public void setPaidAt(Instant paidAt) { this.paidAt = paidAt; }
}
