package io.github.garjelin.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Dates as the API sends them: "yyyy-MM-dd" strings. */
public record BookingDates(
        @JsonProperty("checkin") String checkIn,
        @JsonProperty("checkout") String checkOut) {
}
