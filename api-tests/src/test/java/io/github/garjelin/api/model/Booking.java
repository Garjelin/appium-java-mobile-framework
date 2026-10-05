package io.github.garjelin.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A booking as the API sends and receives it.
 * @JsonProperty maps the API's lowercase JSON names ("firstname") to Java camelCase names,
 * so Java code follows Java conventions and the JSON keeps the API contract.
 * Records give equals() for free: two bookings are equal when all fields are equal,
 * which makes "what we sent == what we got back" a one-line assertion.
 */
public record Booking(
        @JsonProperty("firstname") String firstName,
        @JsonProperty("lastname") String lastName,
        @JsonProperty("totalprice") int totalPrice,
        @JsonProperty("depositpaid") boolean depositPaid,
        @JsonProperty("bookingdates") BookingDates bookingDates,
        @JsonProperty("additionalneeds") String additionalNeeds) {
}
