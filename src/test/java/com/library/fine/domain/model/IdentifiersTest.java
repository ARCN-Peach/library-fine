package com.library.fine.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentifiersTest {

    @Test
    void rental_id_is_value_based_and_rejects_null() {
        UUID value = UUID.randomUUID();
        RentalId a = new RentalId(value);
        RentalId b = new RentalId(value);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a.toString()).isEqualTo(value.toString());

        assertThatThrownBy(() -> new RentalId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("RentalId value");
    }

    @Test
    void user_id_is_value_based_and_rejects_null() {
        UUID value = UUID.randomUUID();
        UserId a = new UserId(value);
        UserId b = new UserId(value);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a.toString()).isEqualTo(value.toString());

        assertThatThrownBy(() -> new UserId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("UserId value");
    }
}
