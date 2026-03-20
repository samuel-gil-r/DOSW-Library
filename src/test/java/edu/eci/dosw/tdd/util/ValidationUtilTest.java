package edu.eci.dosw.tdd.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @Test
    void requireNonBlank_throwsWhenNull() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidationUtil.requireNonBlank(null, "field"));
    }

    @Test
    void requireNonBlank_throwsWhenBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidationUtil.requireNonBlank("   ", "field"));
    }

    @Test
    void requireNonBlank_passesWhenValid() {
        assertDoesNotThrow(() -> ValidationUtil.requireNonBlank("value", "field"));
    }

    @Test
    void requireNonNull_throwsWhenNull() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidationUtil.requireNonNull(null, "field"));
    }

    @Test
    void requireNonNull_passesWhenNonNull() {
        assertDoesNotThrow(() -> ValidationUtil.requireNonNull("anything", "field"));
    }
}
