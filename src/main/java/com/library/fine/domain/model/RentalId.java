package com.library.fine.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class RentalId {

    private final UUID value;

    public RentalId(UUID value) {
        this.value = Objects.requireNonNull(value, "RentalId value must not be null");
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RentalId rentalId = (RentalId) o;
        return Objects.equals(value, rentalId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
