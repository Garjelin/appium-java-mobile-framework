package io.github.garjelin.data;

/** Shipping address form. addressLine2 and state are optional in the app. */
public record Address(
        String fullName,
        String addressLine1,
        String addressLine2,
        String city,
        String zipCode,
        String state,
        String country) {
}
