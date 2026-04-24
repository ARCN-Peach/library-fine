package com.library.fine.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FineIdTest {

    @Test
    void generate_creates_non_null_values_and_distinct_instances() {
        FineId first = FineId.generate();
        FineId second = FineId.generate();

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first.getValue()).isNotNull();
        assertThat(second.getValue()).isNotNull();
        assertThat(first).isNotSameAs(second);
    }

    @Test
    void constructor_rejects_null() {
        assertThatThrownBy(() -> new FineId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("FineId value");
    }

    @Test
    void equals_hashcode_and_to_string_are_value_based() {
        UUID value = UUID.randomUUID();
        FineId a = new FineId(value);
        FineId b = new FineId(value);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a.toString()).isEqualTo(value.toString());
    }
}
