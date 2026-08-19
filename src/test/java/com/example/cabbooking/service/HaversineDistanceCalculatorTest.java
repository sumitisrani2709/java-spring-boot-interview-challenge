package com.example.cabbooking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.example.cabbooking.dto.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HaversineDistanceCalculatorTest {

    private final DistanceCalculator calculator = new HaversineDistanceCalculator();

    @Test
    @DisplayName("distance from a point to itself is zero")
    void zeroDistanceForSamePoint() {
        Location point = new Location(28.6139, 77.2090);

        assertThat(calculator.distanceInKm(point, point)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("one degree of latitude is about 111 km")
    void oneDegreeOfLatitude() {
        double km = calculator.distanceInKm(new Location(0.0, 0.0), new Location(1.0, 0.0));

        assertThat(km).isCloseTo(111.19, within(0.5));
    }

    @Test
    @DisplayName("Delhi to Mumbai is about 1150 km")
    void knownCityPair() {
        double km = calculator.distanceInKm(new Location(28.6139, 77.2090), new Location(19.0760, 72.8777));

        assertThat(km).isCloseTo(1150.0, within(15.0));
    }

    @Test
    @DisplayName("longitude degrees shrink towards the poles")
    void longitudeShrinksWithLatitude() {
        double atEquator = calculator.distanceInKm(new Location(0.0, 0.0), new Location(0.0, 1.0));
        double atSixtyNorth = calculator.distanceInKm(new Location(60.0, 0.0), new Location(60.0, 1.0));

        // cos(60 degrees) == 0.5, so the same longitude delta is roughly half the distance.
        assertThat(atSixtyNorth).isCloseTo(atEquator / 2, within(1.0));
    }

    @Test
    @DisplayName("distance is symmetric")
    void distanceIsSymmetric() {
        Location a = new Location(28.6139, 77.2090);
        Location b = new Location(28.7041, 77.1025);

        assertThat(calculator.distanceInKm(a, b)).isCloseTo(calculator.distanceInKm(b, a), within(1e-9));
    }
}
