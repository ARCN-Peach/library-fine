package com.library.fine.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void creates_money_with_valid_values() {
        Money money = Money.of(new BigDecimal("5.00"), "USD");

        assertThat(money.getAmount()).isEqualByComparingTo("5.00");
        assertThat(money.getCurrency()).isEqualTo("USD");
    }

    @Test
    void normalizes_currency_to_uppercase() {
        Money money = Money.of(new BigDecimal("1.00"), "usd");

        assertThat(money.getCurrency()).isEqualTo("USD");
    }

    @Test
    void allows_zero_amount() {
        Money money = Money.of(BigDecimal.ZERO, "USD");

        assertThat(money.getAmount()).isEqualByComparingTo("0.00");
    }

    @Test
    void rejects_negative_amount() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("-1.00"), "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("non-negative");
    }

    @Test
    void rejects_null_amount() {
        assertThatThrownBy(() -> Money.of(null, "USD"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejects_blank_currency() {
        assertThatThrownBy(() -> Money.of(BigDecimal.ONE, "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Currency");
    }

    @Test
    void equality_is_value_based() {
        Money a = Money.of(new BigDecimal("3.00"), "USD");
        Money b = Money.of(new BigDecimal("3.0"), "USD");

        assertThat(a).isEqualTo(b);
    }
}
