package io.github.garjelin.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Response of POST /booking: the new id plus the stored booking. */
public record CreatedBooking(
        @JsonProperty("bookingid") int bookingId,
        @JsonProperty("booking") Booking booking) {
}
