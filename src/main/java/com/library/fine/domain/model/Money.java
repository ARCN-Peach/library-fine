package com.library.fine.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class Money {

    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency must not be blank");
        }
        this.amount = amount.setScale(2, java.math.RoundingMode.HALF_UP);
        this.currency = currency.toUpperCase();
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
