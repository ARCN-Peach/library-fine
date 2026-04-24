package com.library.fine.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class FineId {

    private final UUID value;

    public FineId(UUID value) {
        this.value = Objects.requireNonNull(value, "FineId value must not be null");
    }

    public static FineId generate() {
        return new FineId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FineId fineId = (FineId) o;
        return Objects.equals(value, fineId.value);
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
