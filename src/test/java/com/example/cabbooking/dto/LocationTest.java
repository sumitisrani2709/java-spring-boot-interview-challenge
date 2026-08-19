package com.example.cabbooking.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.cabbooking.exception.InvalidLocationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LocationTest {

    @Test
    @DisplayName("accepts a valid coordinate pair")
    void acceptsValidCoordinates() {
        Location location = new Location(28.6139, 77.2090);

        assertThat(location.latitude()).isEqualTo(28.6139);
        assertThat(location.longitude()).isEqualTo(77.2090);
    }

    @ParameterizedTest(name = "lat={0}, lon={1} is rejected")
    @CsvSource({
        "91.0, 77.0",
        "-90.5, 77.0",
        "28.6, 180.5",
        "28.6, -180.1"
    })
    @DisplayName("rejects coordinates outside the valid range")
    void rejectsOutOfRangeCoordinates(double latitude, double longitude) {
        assertThatThrownBy(() -> new Location(latitude, longitude))
                .isInstanceOf(InvalidLocationException.class);
    }

    @Test
    @DisplayName("rejects NaN and infinite coordinates")
    void rejectsNonFiniteCoordinates() {
        assertThatThrownBy(() -> new Location(Double.NaN, 77.0)).isInstanceOf(InvalidLocationException.class);
        assertThatThrownBy(() -> new Location(28.6, Double.POSITIVE_INFINITY))
                .isInstanceOf(InvalidLocationException.class);
    }

    @Test
    @DisplayName("accepts the exact boundary values")
    void acceptsBoundaryValues() {
        assertThat(new Location(90.0, 180.0)).isNotNull();
        assertThat(new Location(-90.0, -180.0)).isNotNull();
    }
}
